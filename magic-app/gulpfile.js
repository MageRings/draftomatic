'use strict';

var fs = require('fs');
var path = require('path');

var Promise = require('bluebird');

var gulp = require('gulp');

var autoprefixer = require('gulp-autoprefixer');
var bless = require('gulp-bless');
var del = require('del');
var delAsync = Promise.promisify(require('del'));
var es = require('event-stream');
var filter = require('gulp-filter');
var gulpif = require('gulp-if');
var gutil = require('gulp-util');
var htmlmin = require('gulp-html-minifier');
var inject = require('gulp-inject');
var less = require('gulp-less');
var merge = require('lodash.merge');
var minifyCss = require('gulp-minify-css');
var mkdirp = require('mkdirp');
var ncp = require('ncp');
var rev = require('gulp-rev');
var runSequence = require('run-sequence');
var sourcemaps = require('gulp-sourcemaps');
var ts = require('gulp-typescript');
var uglify = require('gulp-uglify');
var usemin = require('gulp-usemin');
var wiredep = require('wiredep');

Promise.promisifyAll(ncp);
Promise.promisifyAll(mkdirp);

var isWatch = false;

var staticFileTypes = [
  'eot',
  'gif',
  'ico',
  'png',
  'svg',
  'ttf',
  'woff',
  'jpg',
  'html',
  'css'
];

var watchErrorHandler = function (err) {
  if (!isWatch) {
    throw err;
  }
};

var warnMissingFiles = function (files, taskName) {
  files.forEach(function(file) {
    if (!fs.existsSync(file)) {
      gutil.log(
        gutil.colors.cyan(taskName) +
        ' ' +
        gutil.colors.yellow('WARNING:') +
        ' ' +
        gutil.colors.magenta(path.resolve(file)) +
        ' not found.');
    }
  });
};

var staticFileTypesGlob = staticFileTypes.map(function (ext) {
  return '**/*.' + ext;
});

// cleans the build and coverage directories
gulp.task('clean', function (cb) {
  del(['build', 'coverage'], cb);
});

// compiles less into css and lints the css
gulp.task('less', function () {
  var AUTOPREFIXER_BROWSERS = [
    '> 1%',
    'last 2 versions',
    'Firefox ESR',
    'Opera 12.1'
  ];

  return gulp.src('src/**/*.less')
    .pipe(sourcemaps.init())
    .pipe(less())
    // capture errors from the less compiler and suppress when watching
    .on('error', function (err) {
      if (!isWatch) {
        throw err;
      } else {
        // gulp-less is poorly written and will halt the watch pipeline unless
        // the 'end' event is forcefully emitted.
        gutil.log('less ' + gutil.colors.red('Error:') + ' ' + err.message);
        this.emit('end');
      }
    })
    // capture errors from the less reporter and suppress when watching
    .on('error', watchErrorHandler)
    .pipe(autoprefixer(AUTOPREFIXER_BROWSERS))
    .pipe(sourcemaps.write())
    .pipe(gulp.dest('build/src'));
});

// create typescript project outside of task so watches can have incremental builds
var tsProject = ts.createProject({
  removeComments: false,
  noImplicitAny: true,
  declarationFiles: true,
  noExternalResolve: true,
  target: 'ES5',
  typescript: require('typescript') // use typescript 1.5.0-beta
});

// compiles typescript into javascript
gulp.task('typescript', function () {
  // create a filter just for *.ts files
  var tsFilter = filter(['**/*.ts', '!**/*.d.ts']);

  var tsResult = gulp.src([
    'src/**/*.ts',
    'typings/**/*.d.ts'
  ])
  // filter to just *.ts files
  .pipe(tsFilter)
  .pipe(sourcemaps.init())
  // restore *.d.ts files
  .pipe(tsFilter.restore())
  // compile
  .pipe(ts(tsProject))
  // capture errors from the typescript compiler and suppress when watching
  .on('error', watchErrorHandler);

  // merge .js and .d.ts output streams
  return es.merge(tsResult.js, tsResult.dts)
    .pipe(sourcemaps.write())
    .pipe(gulp.dest('build/src'));
});

// create typescript test project outside of task so watches can have incremental builds
var tsTestProject = ts.createProject({
  removeComments: false,
  noImplicitAny: true,
  declarationFiles: false,
  noExternalResolve: true,
  target: 'ES5',
  typescript: require('typescript') // use typescript 1.5.0-beta
});

// compiles typescript from test into javascript
gulp.task('typescript-test', function () {
  // create a filter just for *.ts files
  var tsFilter = filter(['**/*.ts', '!**/*.d.ts']);

  // merge linted ts files and definitions files into a single stream
  return gulp.src([
    'test/**/*.ts',
    'build/src/**/*.d.ts',
    'typings/**/*.d.ts'
  ])
  // filter to just *.ts files
  .pipe(tsFilter)
  .pipe(sourcemaps.init())
  // restore *.d.ts files
  .pipe(tsFilter.restore())
  // compile
  .pipe(ts(tsTestProject))
  // capture errors from the typescript compiler and suppress when watching
  .on('error', watchErrorHandler)
  // we only care about the .js files
  .js
  .pipe(sourcemaps.write())
  .pipe(gulp.dest('build/test'));
});

// copies all static files from the src directory
gulp.task('assets', function () {
  return gulp.src(staticFileTypesGlob, {cwd: 'src'})
    .pipe(gulp.dest('build/src'));
});

// injects required css and javascript into html files
gulp.task('inject', function () {
  // inject is used here instead of wiredep.stream in order
  // to have more control over the relative paths that are
  // injected into the html

  var wiredepFiles = wiredep();
  var json = JSON.parse(fs.readFileSync('bower.json'));

  var main = json.main || [];
  if (typeof main == 'string') {
    main = [main];
  }

  warnMissingFiles(main, 'inject');

  var js = wiredepFiles.js || [];
  var css = wiredepFiles.css || [];

  var files = js.concat(css);

  return gulp.src('src/*.html')
    // inject bower_components files with paths that start with bower_components
    .pipe(inject(gulp.src(files, {read: false, base: '..'}), {addRootSlash: false}))
    // change the relative path of the file so relative inject paths will work in the next step
    .pipe(gulp.dest('build/src'))
    // inject main files which should all be in build/src
    .pipe(inject(gulp.src(main, {read: false}), {relative: true, name: 'self'}))
    .pipe(gulp.dest('build/src'));
});

// copies all files from build/src and dependencies from bower_components into build/tmp
gulp.task('copy-to-tmp', function () {
  // files are copied from build/src in order to avoid modifying
  // any files in build/src when performing minification.
  // an intermediate tmp directory is used.

  // delete min and tmp directories
  return delAsync(['build/tmp', 'build/min']).then(function () {
    return mkdirp.mkdirpAsync('build/tmp/bower_components').then(function () {
      var packages = Object.keys(wiredep().packages);

      return Promise.all([
        // copy src files to tmp
        ncp.ncpAsync('build/src', 'build/tmp'),
        // for all dependent bower components (not devDependencies)
        Promise.all(packages.map(function (pack) {
          var destination = path.join('build/tmp/bower_components', pack);
          var componentDirectory = path.join('bower_components', pack);
          return ncp.ncpAsync(componentDirectory, destination);
        }))]);
    });
  });
});

// copies all static files from build/tmp to build/min
gulp.task('copy-to-min', ['copy-to-tmp'], function () {
  // only copy static files from build/tmp to build/min.
  // all js, html, and css files will be processed by the
  // minify task and output to build/min
  return gulp.src(staticFileTypesGlob, {cwd: 'build/tmp'})
    .pipe(gulp.dest('build/min'));
});

// minifies html, javascript, and css from build/tmp into build/min
gulp.task('minify', ['copy-to-tmp', 'copy-to-min'], function () {
  return gulp.src('build/tmp/*.html')
    .pipe(usemin({
      css: [
        // if you want sourcemaps and aren't using bless, uncomment the next line
        // sourcemaps.init({loadMaps: true}),
        minifyCss({
          // this makes sure that all paths in css files are rewritten
          // to be relative to the directory the html file is in rather
          // than the current working directory
          relativeTo: 'build/tmp',
          target: 'build/tmp'
        }),
        'concat',
        rev(),
        bless({
          cacheBuster: false
        })// ,
        // if you want sourcemaps and aren't using bless, uncomment the next line
        // sourcemaps.write('.')
      ],
      html: [
        htmlmin({
          collapseWhitespace: true
        })
      ],
      js: [
        sourcemaps.init({loadMaps: true}),
        'concat',
        uglify({
          preserveComments: 'some'
        }),
        rev(),
        sourcemaps.write('.')
      ]
    }))
    .pipe(gulp.dest('build/min'));
});

// runs all tasks
gulp.task('default', function (cb) {
  runSequence(
    ['build'],
    ['test'],
    cb);
});

gulp.task('build', function(cb) {
  runSequence(
    ['clean'],
    ['typescript', 'less', 'assets'],
    ['inject'],
    cb);
});

gulp.task('test', ['build'], function(cb) {
  runSequence(
    ['typescript-test'],
    cb);
});

gulp.task('publish', function (cb) {
  runSequence(
    ['default'],
    ['minify'],
    cb);
});

// before running watch, make sure all assets are built
gulp.task('watch', function (cb) {
  isWatch = true;

  runSequence(
    ['typescript', 'less', 'assets'],
    ['typescript-test'],
    ['inject'],
    ['startwatch'],
    cb);
});

gulp.task('startwatch', function () {
  isWatch = true;

  gulp.watch('src/**/*.less', ['less']);

  gulp.watch([
    'src/**/*.ts',
    'typings/**/*.d.ts'
  ], ['typescript']);

  gulp.watch([
    'test/**/*.ts',
    'build/src/**/*.d.ts',
    'typings/**/*.d.ts'
  ], ['typescript-test']);

  gulp.watch(staticFileTypesGlob.map(function (glob) {
    return 'src/' + glob;
  }), ['assets']);

  gulp.watch(['src/*.html', 'bower.json'], ['inject']);
});

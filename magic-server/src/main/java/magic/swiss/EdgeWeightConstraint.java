package magic.swiss;

import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IUndirectedGraphVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.ESat;
import org.chocosolver.util.objects.setDataStructures.ISet;

public class EdgeWeightConstraint extends Propagator<Variable> {

    private static final long serialVersionUID = 4800658011516000735L;
    IUndirectedGraphVar graph;
    IntVar weight;
    int[][] weightMatrix;
    int numNodes;

    public EdgeWeightConstraint(IUndirectedGraphVar graph, IntVar weight, int[][] weightMatrix, int numNodes) {
        super(new Variable[]{graph}, PropagatorPriority.LINEAR, false);
        this.graph = graph;
        this.weight = weight;
        this.weightMatrix = weightMatrix;
        this.numNodes = numNodes;
    }

    @Override
    public void propagate(int evtmask) throws ContradictionException {
        int newMax = 0;
        int newMin = 0;
        for (int i = 0; i < numNodes; i++) {
            ISet potentialNeighbors = graph.getPotNeighOf(i);
            int max = 0;
            int min = Integer.MAX_VALUE;
            System.out.println("Mandatory size:");
            System.out.println(graph.getMandNeighOf(i).getSize());
            for (int j = 0; j < potentialNeighbors.getSize(); j++) {
                if(weightMatrix[i][j] < min) {
                    min = weightMatrix[i][j];
                }
                max += weightMatrix[i][j];
            }
            newMax += max;
            newMin += min;
            System.out.println(newMax);
            System.out.println(newMin);
        }
        //counting everything twice right now
        newMax /= 2;
        newMin /= 2;
        System.out.println(newMax);
        System.out.println(newMin);
        System.out.println("current weight: ");
        System.out.println(weight.getLB());
        System.out.println(weight.getUB());
        weight.updateLowerBound(newMin, this);
        weight.updateUpperBound(newMax, this);
        System.out.println("new weight: ");
        System.out.println(weight.getLB());
        System.out.println(weight.getUB());
    }

    @Override
    public ESat isEntailed() {
        if (weight.isInstantiated() && graph.isInstantiated()) {
            System.out.println("Entailed");
            return ESat.TRUE;
        }
        return ESat.FALSE;
    }

}

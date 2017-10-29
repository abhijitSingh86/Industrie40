package scheduler.algo;

import java.util.Arrays;

public class MunkresAlgorithmImpl {
    public int MAX_VALUE = 99999;
    public static int NA_VALUE = 99998;


    int[][] costArr ;

    int cRows ;
    int cCols ;

    int rowCover[] ;
    int colCover[] ;
    int maskMatrix[][] ;
    int path[][] ;
    int onlyNaRowsCount = 0;
    int step6to4Counter = 0;
    public static void main(String[] args) {
        int[][] costArr = {{18,NA_VALUE,NA_VALUE},{20,NA_VALUE,NA_VALUE},{NA_VALUE,NA_VALUE,NA_VALUE}};
        new MunkresAlgorithmImpl(costArr );
    }
    public MunkresAlgorithmImpl(int[][] costArr){
        this.costArr = costArr;
        //getBalancedCostArray(costArr);
        this.costArr = costArr;
        cRows = costArr.length;
        cCols = costArr[0].length;
        rowCover =new int[cRows];
        colCover =new int[cCols];
        maskMatrix =new int[cRows][cCols];
        path =new int[cRows][cCols];

        start();
    }

    public int[][] getAssignmentmatrix(){
        return maskMatrix;
    }
    private int[][] getBalancedCostArray(int[][] costArr) {

        int max=-1;
        for (int i = 0; i < costArr.length; i++) {
            if(max<costArr[i].length){
                max = costArr[i].length;
            }
        }

        if(max !=-1){
            int[][] cost = new int[costArr.length][max];

            for (int i = 0; i < costArr.length; i++) {
                for (int j = 0; j < max; j++) {

//                    cost[i][j] =
                }
            }
        }

        return new int[0][];
    }
    public void printCovers(){
        for (int j = 0; j < rowCover.length; j++) {
            System.out.format("%5d ",rowCover[j]);
        }
        System.out.println("***********************************");
        for (int j = 0; j < colCover.length; j++) {
            System.out.format("%5d ",colCover[j]);
        }
        System.out.println("***********************************");
    }


    public void printMatrix(int[][] mat){
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                System.out.format("%5d ",mat[i][j]);
            }
            System.out.println("");
        }
        System.out.println("----------------------------------");
    }
    private void start() {

        Arrays.fill(rowCover,0);
        Arrays.fill(colCover,0);
        for (int i = 0; i < costArr.length; i++) {
            Arrays.fill(maskMatrix[i],0);
            Arrays.fill(path[i],0);
        }
        clear_covers();

        int step = 1;
        boolean done = false;
        while(!done) {
            System.out.println("Going to "+step);
            printMatrix(maskMatrix);
            printMatrix(costArr);
            printCovers();
            switch (step) {
                case 1:
                    step = performStep1();
                    break;
                case 2:
                    step = createMaskMatrix_Step2();
                    break;
                case 3:
                    step = checkForSolution_Step3();
                    break;
                case 4:
                    step = recalculateForSolution_Step4();
                    break;
                case 5:
                    step = reShuffelTheMaskMatrix_Step5();
                    break;
                case 6:
                    step = makeUncoveredZero_Step6();
                    break;
                case 7:
                    done=true;
                    break;
                default:
                    done = true;
                    break;
            }
        }


    }



    private int reShuffelTheMaskMatrix_Step5() {
        boolean done = false;
        step6to4Counter = 0;
        int path_count = 1;
        path[path_count-1][0] = forStep5.row;
        path[path_count-1][1] = forStep5.col;

        while(!done){

            Position starPos = null;
            for (int i = 0; i < costArr.length; i++) {
                if(maskMatrix[i][path[path_count-1][1]] ==1){
                    starPos = new Position(i,path[path_count-1][1]);
                    break;
                }
            }
            if(starPos ==null){
                done = true;
            }else{
                path_count+=1;

                path[path_count-1][0] = starPos.row;
                path[path_count-1][1] = path[path_count-2][1];


            }

            if(!done){
                Position prime= null;
                for (int i = 0; i < costArr.length; i++) {
                    if(maskMatrix[path[path_count-1][0]][i] ==2){
                        prime = new Position(path[path_count-1][0],i);
                        break;
                    }
                }

                path_count+=1;
                path[path_count-1][0] = path[path_count-2][0];
                path[path_count-1][1] = prime.col;
            }
        }

        augment_path(path_count);
        clear_covers();
        erase_primes();

        return 3;
    }

    private void erase_primes() {
        for (int i = 0; i < costArr.length; i++) {
            for (int j = 0; j < costArr[i].length; j++) {
                if(maskMatrix[i][j]==2)
                    maskMatrix[i][j]=0;
            }
        }
    }

    private void clear_covers() {
        Arrays.fill(colCover,0);
        Arrays.fill(rowCover,0);
    }

    private void augment_path(int pathCount) {
        for (int i = 0; i < pathCount; i++) {
            if(maskMatrix[path[i][0]][path[i][1]] ==1){
                maskMatrix[path[i][0]][path[i][1]] = 0;
            }else{
                maskMatrix[path[i][0]][path[i][1]] = 1;
            }
        }
    }

    private int makeUncoveredZero_Step6() {
        //find minimum from the uncovered matrix

        int min = MAX_VALUE;
        for (int i = 0; i < costArr.length; i++) {
            for (int j = 0; j < costArr[i].length; j++) {
                if(min > costArr[i][j] && colCover[j] == 0 && rowCover[i] == 0 && costArr[i][j] != NA_VALUE){
                    min = costArr[i][j];
                }
            }
        }

        if(min != MAX_VALUE) {
            for (int i = 0; i < costArr.length; i++) {
                for (int j = 0; j < costArr[i].length; j++) {
                    if (colCover[j] == 0 && costArr[i][j] != NA_VALUE) {
                        costArr[i][j] -= min;
                    }
                    if (rowCover[i] == 1 && costArr[i][j] != NA_VALUE) {
                        costArr[i][j] += min;
                    }
                }
            }
        }
        step6to4Counter+=1;
        return 4;
    }

    class Position{
        int row,col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    Position forStep5=null;

    private int recalculateForSolution_Step4() {
//
        if(step6to4Counter >=2){
            //It means it have reached a stage where whole creation is not working because of NA values.
            return 3;
        }

        boolean done = false;
        while(!done){
            Position pos = null;

            for (int i = 0; i < costArr.length; i++) {
                for (int j = 0; j < costArr[i].length; j++) {
                    if(costArr[i][j] ==0 && rowCover[i]==0 && colCover[j]==0){
                        pos = new Position(i,j);
                        break;
                    }
                }
            }

            if(pos == null){
                //no uncovered zero found so go to 6 to create some
                return 6;
            }else{
                //if found Prime it
                maskMatrix[pos.row][pos.col] = 2;

                boolean found = false;
                //check if in same row there is starred Zero with the uncovered zero
                for (int i = 0; i < costArr[0].length; i++) {
                    if(maskMatrix[pos.row][i] == 1){
                        rowCover[pos.row] = 1;
                        colCover[i]=0;
                        found = true;
                    }
                }

                if(!found){
                    forStep5 = pos;
                    return 5;
                }
            }
        }
        return 6;
    }

    private int checkForSolution_Step3() {

        int colCount = 0;
        int onlyNaRowsCount =0 ;
        int finishCounterForAllOneReq = 0;
        for (int i = 0; i < costArr.length; i++) {
            int na=0;
            for (int j = 0; j < costArr[i].length; j++) {
                if(maskMatrix[i][j]==1) {
                    colCover[j] = 1;
                    colCount++;
                }

                if(costArr[i][j] == NA_VALUE || costArr[i][j] == 0){
                    finishCounterForAllOneReq+=1;
                }
                if(NA_VALUE == costArr[i][j]){
                    na +=1;// costArr[i][j];
                }
            }
            if(na == costArr[i].length){
                onlyNaRowsCount+=1;
            }

        }

        if(finishCounterForAllOneReq == costArr.length*costArr[0].length){
            return 7;
        }

        colCount += onlyNaRowsCount;
        if(colCount >= costArr.length || colCount >= costArr[0].length)
            return 7;

        return 4;
    }


    private int createMaskMatrix_Step2() {



        for (int i = 0; i < costArr.length; i++) {
            for (int j = 0; j < costArr[i].length; j++) {

                if(costArr[i][j] == 0 && rowCover[i] == 0 &&  colCover[j] ==0){
                    maskMatrix[i][j] = 1;
                    rowCover[i]= 1;
                    colCover[j] = 1;
                }

            }
        }
        clear_covers();

        return 3;
    }

    private int performStep1() {

        //find the minimum in the row and substract from the whole row
        //also set if there is any ONLY NA rows

        for (int i = 0; i < costArr.length; i++) {
            int minVal=MAX_VALUE;
            for (int j = 0; j < costArr[i].length; j++) {
                if(minVal>costArr[i][j] && costArr[i][j] !=-1){
                    minVal = costArr[i][j];
                }

            }



            if(minVal != MAX_VALUE){
                for (int j = 0; j < costArr[i].length; j++) {
                    if(costArr[i][j] != NA_VALUE)
                        costArr[i][j] = costArr[i][j] - minVal;
                }
            }
        }

        return 2;
    }
}

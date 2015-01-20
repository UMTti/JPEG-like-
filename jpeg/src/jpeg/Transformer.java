/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpeg;

/**
 *
 * @author pihla
 */
public class Transformer {
    
    /**
     *
     */
    public double[][][][] blocks;

    private int n;
    private double[] c;
    /**
     * 
     * @param blocks
     */
    public Transformer(double[][][][] blocks){
        this.blocks = blocks;
        this.n= 8;
        this.initializeCoefficients();
        
    }
    
    private void initializeCoefficients() {
        this.c = new double[this.n];
        for (int i=1;i<this.n;i++) {
            c[i]=1;
        }
        c[0]=1/Math.sqrt(2.0);
    }

    
    
    /**
     * Does a function given a parameter for all 8*8 blocks and saves block back
     * @param blocks
     * @param command
     * @return 
     */
    public double[][][][] doForBlocks(double[][][][] blocks, String command){
        // komennoiksi multiplyWithCosines ja applyIDCT
        for(int i = 0;i<blocks.length;i++){
            //täällä on elikkä niitä 8 arrayta joissa kaikissa on 3 tasoa
            double[][] y = new double[8][8];
            double[][] cb = new double[8][8];
            double[][] cr = new double[8][8];
            for(int j = 0;j<blocks[i].length;j++){
                for(int k = 0;k<blocks[j].length;k++){
                    y[j][k] = blocks[i][j][k][0];
                    cb[j][k] = blocks[i][j][k][1];
                    cr[j][k] = blocks[i][j][k][2];
                }
            }
            if(command.equals("applyIDCT")){
                y = applyIDCT(y);
                cb = applyIDCT(cb);
                cr = applyIDCT(cr);
            } else if(command.equals("multiplyWithCosines")){
                y = multiplyWithCosines(y);
                cb = multiplyWithCosines(cb);
                cr = multiplyWithCosines(cr);
            }
            
            for(int j = 0;j<blocks[i].length;j++){
                for(int k = 0;k<blocks[j].length;k++){
                    blocks[i][j][k][0] = y[j][k];
                    blocks[i][j][k][1] = cb[j][k];
                    blocks[i][j][k][2] = cr[j][k];
                }
            }
        }
        return blocks;
    }
    
    /**
     * Multiply a single 8*8 block with cosines
     * @param f
     * @return
     */
    
    public double[][] multiplyWithCosines(double[][] f) {
        int n = this.n;
        double[][] F = new double[n][n];
        for (int u=0;u<n;u++) {
          for (int v=0;v<n;v++) {
            double sum = 0.0;
            for (int i=0;i<n;i++) {
              for (int j=0;j<n;j++) {
                sum+=Math.cos(((2*i+1)/(2.0*n))*u*Math.PI)*Math.cos(((2*j+1)/(2.0*n))*v*Math.PI)*f[i][j];
              }
            }
            sum*=((c[u]*c[v])/4.0);
            F[u][v]=sum;
          }
        }
        return F;
    }
    
    /**
     * Reverse DCT cosine multiplying for 8*8 block
     * @param F
     * @return
     */
    public double[][] applyIDCT(double[][] F) {
        int n = this.n;
        double[][] f = new double[n][n];
        for (int i=0;i<n;i++) {
          for (int j=0;j<n;j++) {
            double sum = 0.0;
            for (int u=0;u<n;u++) {
              for (int v=0;v<n;v++) {
                sum+=(c[u]*c[v])/4.0*Math.cos(((2*i+1)/(2.0*n))*u*Math.PI)*Math.cos(((2*j+1)/(2.0*n))*v*Math.PI)*F[u][v];
              }
            }
            f[i][j]=Math.round(sum);
          }
        }
        return f;
    }

    
}

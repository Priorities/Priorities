package priorities;

import java.io.File;

/**
 *
 * @author Weiwei Chen
 */

public class ConverterOptions implements Cloneable{
    /**
     * The argument string with which the converter was invoked.
     * 
     */
    public ConverterOptions(){
        mOriginalArgumentString = null;
        mInputDAX               = null;
        mOutputDAX              = null;
        mAlgorithm              = null;
        mNodeList               = null;
        mDepth                  = -1;
        mReverse                = false;
        mJobMapMode             = "direct";
        mPriority               = "dagman";
        mVersion                = "1.0.3";
    }
    /** The version of this program. */
    private String mVersion;
    
    private String mPriority;
    
    /** The argument. */
    private String mOriginalArgumentString;
    
    private String mInputDAX;
    
    private String mOutputDAX;
    
    private String mAlgorithm;
     
    private String mNodeList;
    
    private int mDepth;
    
    private boolean mReverse;
    
    private String mJobMapMode;
    
    public void setNodeList(String list){
        this.mNodeList = list;
    }
    public String getNodeList(){
        return this.mNodeList;
    }
    
    public void setInputDAX(String dax){
        this.mInputDAX = dax;
    }
    public String getInputDAX(){
        return this.mInputDAX;
    }
    
    public void setOutputDAX(String dax)
    {
        this.mOutputDAX = dax;
    }
    
    public String getOutputDAX(){
        return this.mOutputDAX;
    }
    
    public void setAlgorithm(String algm){
        this.mAlgorithm = algm;
    }
    public String getAlgorithm()
    {
        return this.mAlgorithm.toLowerCase();
    }
    public void setDepth(int depth){
        this.mDepth = depth;
    }
    public int getDepth(){
        return this.mDepth;
    }
    
    public void setReverse(boolean reverse){
        this.mReverse = reverse;
    }
    public boolean getReverse(){
        return this.mReverse;
    }
    public void setJobMapMode(String mode){
        this.mJobMapMode = mode.toLowerCase();
    }
    public String getJobMapMode(){
        return this.mJobMapMode;
    }
    public String getPriority(){
        return this.mPriority;
    }
    public void setPriority(String mode){
        this.mPriority  = mode.toLowerCase();
    }
    public String getVersion(){
        return this.mVersion;
    }
        /**
         * 
     * Sets the argument string of how planner was invoked. This function
     * just stores the arguments as a String internally.
     * 
     * @param args   the arguments with which the planner was invoked.
     */
    public void setOriginalArgString( String[] args ) {
        
        StringBuffer originalArgs = new StringBuffer();
        for( int i = 0; i < args.length ; i++ ){
            originalArgs.append( args[i] ).append( " " );
        }
        this.mOriginalArgumentString = originalArgs.toString();
    }
    
    public boolean checkParams(){
        if(this.mOriginalArgumentString == null) return false;
        if(this.mInputDAX==null) return false;
        File f = new File(this.mInputDAX);
        if(!f.exists() || !f.canRead())return false;
        if(this.mOutputDAX == null)return false;
        if(getAlgorithm().equals("backward") && this.mDepth < 0)return false;
        if(this.mAlgorithm == null)return false;
        if(!this.mPriority.equals("dagman")||this.mPriority.equals("condor"))return false;
        return true;
    }
}

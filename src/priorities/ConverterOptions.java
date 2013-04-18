/**
 *  Copyright 2012-2013 University Of Southern California
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package priorities;

import java.io.File;

/**
 * ConverterOptions stores all the parameters
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
    
    /** The input dax path. */
    private String mInputDAX;
    
    /** The output dax path. */
    private String mOutputDAX;
    
    /** The algorithm used to parse workflows. */
    private String mAlgorithm;
     
    /** All the node list. */
    private String mNodeList;
    
    /** The max depth. */
    private int mDepth;
    
    /** Whether should we reverse the ranking. */
    private boolean mReverse;
    
    
    private String mJobMapMode;
    
    /**
     * Sets the node list
     * @param list the node list
     */
    public void setNodeList(String list){
        this.mNodeList = list;
    }
    
    /**
     * Gets the node list
     * @return the node list
     */
    public String getNodeList(){
        return this.mNodeList;
    }
    
    /**
     * Sets the input dax path
     * @param dax the input dax
     */
    public void setInputDAX(String dax){
        this.mInputDAX = dax;
    }
    
    /**
     * Gets the input dax path
     * @return the input dax
     */
    public String getInputDAX(){
        return this.mInputDAX;
    }
    
    /**
     * Sets the output dax
     * @param dax the output dax
     */
    public void setOutputDAX(String dax)
    {
        this.mOutputDAX = dax;
    }
    
    /**
     * Gets the output dax
     * @return the output dax
     */
    public String getOutputDAX(){
        return this.mOutputDAX;
    }
    
    /**
     * Sets the algorithm
     * @param algm the algorithm
     */
    public void setAlgorithm(String algm){
        this.mAlgorithm = algm;
    }
    
    /**
     * Gets the algorithm
     * @return the algorithm
     */
    public String getAlgorithm()
    {
        return this.mAlgorithm.toLowerCase();
    }
    
    /**
     * Sets the depth
     * @param depth the depth
     */
    public void setDepth(int depth){
        this.mDepth = depth;
    }
    
    /**
     * Gets the depth
     * @return the depth
     */
    public int getDepth(){
        return this.mDepth;
    }
    
    /**
     * Sets the reverse
     * @param reverse the reverse 
     */
    public void setReverse(boolean reverse){
        this.mReverse = reverse;
    }
    
    /**
     * Gets the reverse
     * @return the reverse
     */
    public boolean getReverse(){
        return this.mReverse;
    }
    
    /**
     * Sets the job map mode
     * @param mode the job map mode
     */
    public void setJobMapMode(String mode){
        this.mJobMapMode = mode.toLowerCase();
    }
    public String getJobMapMode(){
        return this.mJobMapMode;
    }
    
    /**
     * Gets the priority
     * @return the priority
     */
    public String getPriority(){
        return this.mPriority;
    }
    
    /**
     * Sets the priority (case insensitive)
     * @param mode the priority
     */
    public void setPriority(String mode){
        this.mPriority  = mode.toLowerCase();
    }
    
    /**
     * Gest the version 
     * @return the version
     */
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
    
    /**
     * Checks whether all required parameters are set
     * @return 
     */
    public boolean checkParams(){
        if(this.mOriginalArgumentString == null) {
            return false;
        }
        if(this.mInputDAX==null) {
            return false;
        }
        File f = new File(this.mInputDAX);
        if(!f.exists() || !f.canRead()){
            return false;
        }
        if(this.mOutputDAX == null){
            return false;
        }
        if(getAlgorithm().equals("backward") && this.mDepth < 0){
            return false;
        }
        if(this.mAlgorithm == null){
            return false;
        }
        if(!this.mPriority.equals("dagman") && !this.mPriority.equals("condor")){
            return false;
        }
        return true;
    }
}

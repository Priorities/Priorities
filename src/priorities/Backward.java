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

import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;

/**
 *
 * @author Weiwei Chen
 */
public class Backward extends Parser{
    
    public Backward (Map jobMap,  Document doc, ConverterOptions options){
        super( jobMap, doc, options);
    }
    
    @Override
    public void run()
    {
        
        AbstractNode sink = getSink();
        if(this.getOptions().getJobMapMode().equals("montage"))
        {
            removeDuplicateMontage();
        }else{
            getParents(sink);
            removeDuplicate(sink);
        }
        System.out.println("Duplicated Dependencies are removed");
        int depth = this.getOptions().getDepth();
        setCheck(sink, false);
        Backward(sink, 1, depth);
        clean();//not necessary but just make it complete
    }
    

    
    //step now is the max priority of this subtree
    private int Backward(AbstractNode node, int step, int depth){
        
        if(node.hasChecked){
            return step;
        }
        this.addPriority(node, Integer.toString(step));
        node.hasChecked = true;
        
        if(node.getParents().isEmpty()){

        }else if(node.getParents().size()==1){
            //the same parameter
            step = Backward(node.getParents().get(0), step, depth);
        }else if(depth<=0)
        {   
            for(Iterator it  = node.getParents().iterator(); it.hasNext();){
                AbstractNode pNode = (AbstractNode)it.next();
                step = Backward(pNode, step, depth);
            }
            
        }else{
            depth --;
            for(Iterator it  = node.getParents().iterator(); it.hasNext();){
                AbstractNode pNode = (AbstractNode)it.next();
                step = Backward(pNode, step, depth);
                step ++;
            }
            step --;
        }
        
        return step;
    }
    
   
}

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
 * Iterate through dax with DFS algorithm
 * @author Weiwei Chen
 */
public class DFSParser extends Parser{
    
    /**
     * Initialize a Backward object
     * @param jobMap the job map
     * @param doc the DOM document
     * @param options parameters
     */
    public DFSParser (Map jobMap, Document doc, ConverterOptions options){
        super( jobMap,   doc, options);
    }
        
    /**
     * The main function
     */
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
        setCheck(sink, false);
        clean(); 
        
        AbstractNode root = getRoot();
        DFS(root, 0);
        clean();//not necessary but just make it complete
    }
    /**
     * Iterate through the workflow
     * @param node the root node
     * @param step the steps
     * @return 
     */
    private int DFS(AbstractNode node, int step)
    {
        if(node.hasChecked){
            return step;
        }
        
        step ++;
        
        this.addPriority(node, Integer.toString(step));
        node.hasChecked = true;
        for(Iterator it = node.getChildren().iterator();it.hasNext();){
            AbstractNode cNode = (AbstractNode)it.next();
            step = DFS(cNode, step);
            
        }
        return step;
    }
}

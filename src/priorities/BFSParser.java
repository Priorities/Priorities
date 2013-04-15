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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.w3c.dom.Document;

/**
 * This is a parser using BFS algorithm
 * 
 * @author Weiwei Chen
 */
public class BFSParser extends Parser{
    
    /**
     * Initialize a BFSParser object
     * @param jobMap the map stores the name and the node
     * @param doc the DOM document object
     * @param options all parameters
     */
    public BFSParser (Map jobMap, Document doc, ConverterOptions options){
        super( jobMap,  doc, options);
    }
    
    /**
     * The main function
     */
    @Override
    public void run()
    {
        AbstractNode root = getRoot();
        BFS(root, 0);
        clean();
    }

    /**
     * Iterate the graph using BFS
     * @param node the root node
     * @param step the depth of this node
     * @return 
     */
    private int BFS(AbstractNode node, int step)
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

        Queue<AbstractNode> queue = new LinkedList<AbstractNode>();
        queue.add(node);
        while(!queue.isEmpty())
        {

            node = queue.poll();
            if(node.hasChecked){
                continue;
            }
            
            step ++;

            node.hasChecked = true;
            this.addPriority(node, Integer.toString(step));
            for(Iterator it = node.getChildren().iterator();it.hasNext();){
                AbstractNode cNode = (AbstractNode)it.next();
                queue.add(cNode);
               
            }
        }
        step ++;
        return step;
    }
}

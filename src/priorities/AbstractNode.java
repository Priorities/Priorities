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

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

/**
 * AbstractNode is a implementation of a node in DAX.
 * 
 * @author Weiwei Chen
 */
public class AbstractNode {
    /**DOM object of node. */
    private Element node;
    /**Parent nodes. */
    private List<AbstractNode> parentList;
    /**Child nodes. */
    private List<AbstractNode> childList;
    /**check point. */
    public boolean hasChecked;
    
    
    /**
     * Gets the DOM object of node
     * @return org.w3c.dom.Element
     */
    public Element getNode(){
        return this.node;
    }
    /**
     * Gets the parent list
     * @return parent list
     */
    public List<AbstractNode> getParents(){
        return this.parentList;
    }
    /**
     * Gets child list
     * @return child list
     */
    public List<AbstractNode> getChildren(){
        return this.childList;
    }
    /**
     * Adds a parent node 
     * @param pNode the parent node 
     * @return whether this node has a parent before
     */
    public boolean addParent(AbstractNode pNode){
        if(parentList!=null){
            parentList.add(pNode);
            return true;
        }
        return false;
    }
    /**
     * Adds a child node
     * @param cNode the child node
     * @return whether this node has a child before
     */
    public boolean addChild(AbstractNode cNode){
        if(childList!=null){
            childList.add(cNode);
            return true;
        }
        return false;
    }
    /**
     * Initialize an AbstractNode object
     * @param node the Element 
     */
    public AbstractNode(Element node){
        this.node = node;
        this.parentList = new ArrayList<AbstractNode>();
        this.childList  = new ArrayList<AbstractNode>();
        this.hasChecked = false;
    }
}

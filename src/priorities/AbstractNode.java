package priorities;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Weiwei Chen
 */
public class AbstractNode {
    private Element node;
    private List<AbstractNode> parentList;
    private List<AbstractNode> childList;
    public boolean hasChecked;
    
    
    
    public Element getNode(){
        return this.node;
    }
    public List<AbstractNode> getParents(){
        return this.parentList;
    }
    public List<AbstractNode> getChildren(){
        return this.childList;
    }
    public boolean addParent(AbstractNode pNode){
        if(parentList!=null){
            parentList.add(pNode);
            return true;
        }
        return false;
    }
    public boolean addChild(AbstractNode cNode){
        if(childList!=null){
            childList.add(cNode);
            return true;
        }
        return false;
    }
    public AbstractNode(Element node){
        this.node = node;
        this.parentList = new ArrayList<AbstractNode>();
        this.childList  = new ArrayList<AbstractNode>();
        this.hasChecked = false;
    }
}

package priorities;

import java.util.ArrayList;
import java.util.HashMap;
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

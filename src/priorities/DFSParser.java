package priorities;

import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;

/**
 *
 * @author Weiwei Chen
 */
public class DFSParser extends Parser{
    
    public DFSParser (Map jobMap, Document doc, ConverterOptions options){
        super( jobMap,   doc, options);
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
        setCheck(sink, false);
        clean(); 
        
        AbstractNode root = getRoot();
        DFS(root, 0);
        clean();//not necessary but just make it complete
    }
    
    private int DFS(AbstractNode node, int step)
    {
        if(node.hasChecked)return step;
        
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

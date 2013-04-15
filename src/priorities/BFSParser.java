package priorities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.w3c.dom.Document;

/**
 *
 * @author Weiwei Chen
 */
public class BFSParser extends Parser{
    
    public BFSParser (Map jobMap, Document doc, ConverterOptions options){
        super( jobMap,  doc, options);
    }
    
    @Override
    public void run()
    {
        AbstractNode root = getRoot();
        BFS(root, 0);
        clean();
    }

    
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
//            if(node.hasChecked)
//            {
//                queue.remove(node);//very important
//                continue;
//            }
            node = queue.poll();
            if(node.hasChecked)continue;
            
            step ++;
//            if(step==14)
//                System.out.println(step);
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

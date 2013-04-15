package priorities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Weiwei Chen
 */
public class Parser {
    
    /**
     * jobMap should be ok
     */
    private Map<String, AbstractNode> jobMap;
    //private Map<AbstractNode, String> idMap;//maybe not useful
    private Document doc;
    private AbstractNode root;
    private AbstractNode sink;
    private ConverterOptions options;
    
    private File tmpMapFile ;
    /**
     * parentMap must be scalable
     */
    private Map<AbstractNode, ArrayList> parentMap 
            = new HashMap<AbstractNode, ArrayList>();

    public Parser(Map jobMap, Document doc, ConverterOptions options)
    {
        this.jobMap = jobMap;
        
        //System.out.println("size is" + idMap.size());
        this.doc    = doc;
        this.options= options;
        try{
            this.tmpMapFile = File.createTempFile("jobmap_", "", new File("/tmp"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    
    public String getId(AbstractNode node){
        String id = "";
        id = node.getNode().getAttribute("id");
        return id;
    }
    
    public AbstractNode getNode(String id){
        AbstractNode node = null;
        try{
            if(jobMap.containsKey(id)){
                node = (AbstractNode)jobMap.get(id);
            }else{
                System.out.println("Error: cannot find id:" + id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return node;
    }
    
    public void deleteTmpFiles(){
        if(tmpMapFile!=null && tmpMapFile.exists()){
            //delete temp files
            boolean success = tmpMapFile.delete();
            if(!success){
                System.out.println("Deletion Failed");
            }
        }
    }
    
    public void setOptions(ConverterOptions options){
        this.options = options;
    }
    public ConverterOptions getOptions(){
        return this.options;
    }
    protected void addPriority(AbstractNode node, String priority)
    {
        addPriority(node.getNode(), priority, doc);
    }
    
    private void addPriority(Element el , String priority, Document doc)
    {
        
        if(this.getOptions().getReverse()){
            int pri = Integer.parseInt(priority);
            int max = this.jobMap.size() + 2;
            pri = max - pri;
            priority = Integer.toString(pri);
        }
        
         Element nel = doc.createElement("profile");
                    
         nel.appendChild(doc.createTextNode(priority));
         
         nel.setAttribute("namespace", options.getPriority());
         nel.setAttribute("key", "priority");
                    
         Node cld = el.getChildNodes().item(2);
         el.insertBefore(nel, cld);
    }

    
    public void setCheck(AbstractNode node, boolean value){
        node.hasChecked = value;
        for(Iterator it = node.getParents().iterator();it.hasNext();){
            AbstractNode pNode = (AbstractNode)it.next();
            setCheck(pNode, value);
        }
    }
    
    public void removeDuplicateMontage(){

        List jobList = new ArrayList(jobMap.values());
        for(int i = 0; i < jobList.size(); i++){
            AbstractNode node = (AbstractNode)jobList.get(i);
            String name = node.getNode().getAttribute("name");
            if(name.equals("mBackground")){
                //remove all of its parents of mProjectPP
                
                 for(int j = 0; j < node.getParents().size(); j++){
                     
                    AbstractNode parent = (AbstractNode)node.getParents().get(j);
                    if(parent.getNode().getAttribute("name").equals("mProjectPP")){
                        j--;
                        node.getParents().remove(parent);
                        parent.getChildren().remove(node);
                    }
                 }
                
            }else if(name.equals("mAdd")){
                 for(int j = 0; j < node.getParents().size(); j++){
                     
                    AbstractNode parent = (AbstractNode)node.getParents().get(j);
                    String pName = parent.getNode().getAttribute("name");
                    if(pName.equals("mBackground")||pName.equals("mShrink")){
                        j--;
                        node.getParents().remove(parent);
                        parent.getChildren().remove(node);
                    }
                 }                
            }
        }
        
    }
    
    
    public void removeDuplicate(AbstractNode node){
        //use DFS to check if has been check
        
        //if only one doesn't need to check
        if(node.hasChecked)return;
        if(node.getParents().size() == 1)
            removeDuplicate(node.getParents().get(0));
        for(int i = 0; i < node.getParents().size(); i ++){
            AbstractNode pNode = (AbstractNode)node.getParents().get(i);
            
            for(int j = 0; j < node.getParents().size(); j++){
                if(i==j)continue;
                AbstractNode parent = (AbstractNode)node.getParents().get(j);
                //if(checkParent(parent, pNode)){
                if(getParents(parent).contains(pNode)){
                    //remove pNode <-> node
                    i--;
                    node.getParents().remove(pNode);
                    pNode.getChildren().remove(node);
                }
            }
            removeDuplicate(pNode);
        }
        node.hasChecked = true;
    }
    
    
    private void saveParentsToFile(AbstractNode node, ArrayList list){
        //similar to parentMap.put(node, list)
        
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(tmpMapFile,true));
            String line = getId(node);
            if(!line.equals("")){//make sure id is not ""

                
                for(Iterator it = list.iterator();it.hasNext();){
                    
                        //a typcical bug here
                    String item = getId((AbstractNode)it.next());
                    if(item.equals("")){
                        System.out.println("What wrong");
                    }
                    line += " " + item;
                }
                line += "\n";
            
                out.write(line );
            }
            out.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private ArrayList getParentsFromFile(AbstractNode aNode){
        //if id == "" return empty list
        ArrayList list = null;
        String id = getId(aNode);
        if(id.equals("")){
            list = new ArrayList();
            return list;
        }
        try{
            BufferedReader in = new BufferedReader(new FileReader(tmpMapFile));
            String line;
            boolean hasFound  = false;
            while((line = in.readLine())!=null){
                String[] items = line.split(" ");
                //items[0] should exist
                if(items.length<1){
                    System.out.println("Error Here");
                    System.exit(1);
                }
                String cId = items[0];
                if(cId.equals(id)){
                    hasFound = true;
                    list = new ArrayList();
                    for(int i = 1; i < items.length; i++){
                        String pId = items[i];
                        AbstractNode node = getNode(pId);
                        list.add(node);
                    }
                    break;
                }
                
            }
            
            in.close();
            if(hasFound){
                return list;
            }else{
                return null;
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    private void addAllWithoutDuplicate(List srcList, List destList){
        if(srcList!=null && destList != null){
            for(Iterator it = destList.iterator(); it.hasNext(); ){
                AbstractNode obj = (AbstractNode)it.next();
                if(!srcList.contains(obj)){
                    srcList.add(obj);
                }

            }
        }
    }
    /*
     * Should not use too much memory here
     */
    
    public ArrayList getParents(AbstractNode node){
//        if(parentMap.containsKey(node)){
//            return (ArrayList)parentMap.get(node);
//        }
        
        ArrayList newList = null;
        if(options.getJobMapMode().equals("file")){
            getParentsFromFile(node);
        }else if(options.getJobMapMode().equals("direct")) {
            newList = null;
        }else if(options.getJobMapMode().equals("memory")) {
            if(parentMap.containsKey(node)){
                newList = (ArrayList)(parentMap.get(node));
                
            }else{
                //still null
            }
        }else{
            System.out.println("Error in job map mode");
            System.exit(1);
        }
        //ArrayList newList = null;
        if(newList!=null){
            return newList;//it's ok if it's empty but not null
        }else{
        
            ArrayList list = new ArrayList<AbstractNode>();
            
            //list.addAll(node.getParents());
            addAllWithoutDuplicate(list, node.getParents());
            for(Iterator it  = node.getParents().iterator(); it.hasNext(); ){
                AbstractNode pNode = (AbstractNode)it.next();
                ArrayList pList = (ArrayList)getParents(pNode);
                //list.addAll(pList);
                addAllWithoutDuplicate(list, pList);
            }
            if(options.getJobMapMode().equals("memory")){
                parentMap.put(node, list);
            }else if(options.getJobMapMode().equals("file")){
                saveParentsToFile(node, list);
            }else if(options.getJobMapMode().equals("direct")){
                //do nothing
            }
            return list;
        }
    }

    
    //root is not part of jobMap
    public AbstractNode getRoot(){
        if(root==null){
            Element el = doc.createElement("root");
            el.setAttribute("id", "head");
            
            root = new AbstractNode(el);
            
            for(Iterator it = jobMap.values().iterator(); it.hasNext();){
                AbstractNode node = (AbstractNode)it.next();
                if(node.getParents().isEmpty()){
                    node.addParent(root);
                    root.addChild(node);
                }
            }
            jobMap.put( el.getAttribute("id"), root);
            
        }
        return root;
    }
    public AbstractNode getSink(){
        if(sink==null){
            Element el = doc.createElement("sink");
            el.setAttribute("id", "sink");
            sink = new AbstractNode(el);

            for(Iterator it = jobMap.values().iterator(); it.hasNext();){
                AbstractNode node = (AbstractNode)it.next();
                if(node.getChildren().isEmpty()){
                    node.addChild(sink);
                    sink.addParent(node);
                }
            }
            //a typical error here
            jobMap.put( el.getAttribute("id"), sink);
        }
        return sink;
    }
    
    public void run(){
          
       for(Iterator it = jobMap.values().iterator(); it.hasNext();){
            AbstractNode node = (AbstractNode)it.next();
            Element el = node.getNode();

            addPriority(el, "999", doc);
        }
            
    }
    
    public void clean(){
        if(root!=null){
            if(root.getNode().hasAttribute("id")){
                root.getNode().removeAttribute("id");
            }
            if(jobMap.containsKey("head")){
                jobMap.remove("head");
                
            }
            for(Iterator it = jobMap.values().iterator(); it.hasNext();){
                AbstractNode node = (AbstractNode)it.next();
                if(node.getParents().contains(root)){//make sure there is only one parent is root
                    node.getParents().remove(root);
                    root.getChildren().remove(node);
                }
            }
            root = null;
        }
        if(sink!=null){
            if(sink.getNode().hasAttribute("id")){
                sink.getNode().removeAttribute("id");
            }
            if(jobMap.containsKey("sink")){
                jobMap.remove("sink");
            }
            for(Iterator it = jobMap.values().iterator(); it.hasNext();){
                AbstractNode node = (AbstractNode)it.next();
                if(node.getChildren().contains(sink)){//make sure there is only one parent is root
                    node.getChildren().remove(sink);
                    sink.getChildren().remove(node);
                }
            }
            sink = null;
        }
    }
    
    public Map getJobMap(){
        return this.jobMap;
    }
}

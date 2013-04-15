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
 * A basic implementation of parser
 * @author Weiwei Chen
 */
public class Parser {
    

    /** The map from the name to the object. */
    private Map<String, AbstractNode> jobMap;
    /** DOM document object. */
    private Document doc;
    /** Root node. */
    private AbstractNode root;
    /** Sink node. */
    private AbstractNode sink;
    /** All parameters. */
    private ConverterOptions options;
    /** Temp file. */
    private File tmpMapFile ;
    /**
     * parentMap must be scalable
     */
    private Map<AbstractNode, ArrayList> parentMap 
            = new HashMap<AbstractNode, ArrayList>();

    /**
     * Initialize a Parser object
     * @param jobMap the job map
     * @param doc the DOM document object
     * @param options the arguments
     */
    public Parser(Map jobMap, Document doc, ConverterOptions options)
    {
        this.jobMap = jobMap;
        
        this.doc    = doc;
        this.options= options;
        try{
            this.tmpMapFile = File.createTempFile("jobmap_", "", new File("/tmp"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the name of a node
     * @param node the node
     * @return the name
     */
    public String getId(AbstractNode node){
        String id = "";
        id = node.getNode().getAttribute("id");
        return id;
    }
    
    /**
     * Gets the node 
     * @param id the name of the node
     * @return the node
     */
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
    /**
     * Delete temp file
     */
    public void deleteTmpFiles(){
        if(tmpMapFile!=null && tmpMapFile.exists()){
            //delete temp files
            boolean success = tmpMapFile.delete();
            if(!success){
                System.out.println("Deletion Failed");
            }
        }
    }
    /**
     * Sets the options
     * @param options 
     */
    public void setOptions(ConverterOptions options){
        this.options = options;
    }
    /**
     * Gets the options 
     * @return options
     */
    public ConverterOptions getOptions(){
        return this.options;
    }
    /**
     * Add a priority to a node
     * @param node the node 
     * @param priority the priority
     */
    protected void addPriority(AbstractNode node, String priority)
    {
        addPriority(node.getNode(), priority, doc);
    }
    
    /**
     * Add a priority to a Element
     * @param el the element
     * @param priority the priority
     * @param doc  the document object
     */
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

    /**
     * Sets the check point
     * @param node the node
     * @param value true or false
     */
    public void setCheck(AbstractNode node, boolean value){
        node.hasChecked = value;
        for(Iterator it = node.getParents().iterator();it.hasNext();){
            AbstractNode pNode = (AbstractNode)it.next();
            setCheck(pNode, value);
        }
    }
    /**
     * This is specially designed for Montage workflow because Montage has duplicate dependencies
     */
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
    
    /**
     * using DFS to check if it has duplicate but this takes much time
     * @param node 
     */
    public void removeDuplicate(AbstractNode node){
        //use DFS to check if has been check
        
        //if only one doesn't need to check
        if(node.hasChecked){
            return;
        }
        if(node.getParents().size() == 1){
            removeDuplicate(node.getParents().get(0));
        }
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
    
    /**
     * Save all objects to files
     * @param node the AbstractNode object
     * @param list the list of AbstractNode
     */
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
    /**
     * Gets parents from files
     * @param aNode 
     * @return 
     */
    private ArrayList getParentsFromFile(AbstractNode aNode){
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
    /**
     * 
     * @param srcList
     * @param destList 
     */
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

    
    public ArrayList getParents(AbstractNode node){

        
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
        if(newList!=null){
            return newList;//it's ok if it's empty but not null
        }else{
        
            ArrayList list = new ArrayList<AbstractNode>();
            
            addAllWithoutDuplicate(list, node.getParents());
            for(Iterator it  = node.getParents().iterator(); it.hasNext(); ){
                AbstractNode pNode = (AbstractNode)it.next();
                ArrayList pList = (ArrayList)getParents(pNode);
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

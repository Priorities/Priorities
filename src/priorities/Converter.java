package priorities;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Weiwei Chen
 */
public class Converter {
    
    
    private  Map<String, AbstractNode> jobMap;
    //private  Map<AbstractNode, String> idMap;
    public Converter()
    {
        jobMap = new HashMap<String, AbstractNode>();
        //idMap  = new HashMap< AbstractNode, String>(); 
    }
    
    
    private  AbstractNode getElement(String id){
        if(jobMap!=null && jobMap.size()>0){
            return (AbstractNode)jobMap.get(id);
        }
        return null;
    }
    

    public void execute(String[] args){
        long startTime = System.currentTimeMillis();
                
        ConverterOptions options = parseCommandLineArguments(args);
        System.out.println("Parsing Files ...");
        parseXmlFile(options);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
//        int hrs = (int)(duration/1000/3600);
//        int min = (int)(duration/1000%3600/60);
//        int sec = (int)(duration/1000-hrs*3600-min*60);
//        String outStr = String.format("%d hours %d min %d sec", hrs, min, sec);
        String outStr =String.format("%d hours, %d min, %d sec", TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.MILLISECONDS.toHours(duration)*60,
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MILLISECONDS.toMinutes(duration)*60);
        System.out.println("The program takes " + outStr);
    }
    public void parseXmlFile(ConverterOptions options){
        DocumentBuilderFactory dbf  = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder db =dbf.newDocumentBuilder();
            Document doc = db.parse(options.getInputDAX());
            //Document doc = db.parse("/Users/chenweiwei/Downloads/store/montage/dags/dag.xml");
            //Document doc = db.parse("/Users/chenweiwei/NetBeansProjects/Priorities/examples/montage/0.5degree/dag.xml");
            Element docEle= doc.getDocumentElement();
            NodeList nl = docEle.getElementsByTagName("job");
            
            if(nl != null && nl.getLength() > 0){
                for(int i = 0; i < nl.getLength(); i ++){
                    Element el = (Element)nl.item(i);
                    String id = el.getAttribute("id");
                    AbstractNode node = new AbstractNode(el);
                    jobMap.put(id, node);
                    //System.out.println("id= " + node.getNode().getAttribute("id"));
                    //idMap.put(node, id);
                }
            }
             
            NodeList cl = docEle.getElementsByTagName("child");
            if(cl!=null && cl.getLength()>0){
                for (int i = 0; i < cl.getLength(); i++){
                    Element el = (Element)cl.item(i);
                    String cRef = el.getAttribute("ref");
                    
                    AbstractNode childNode = getElement(cRef);
                    NodeList cls = el.getElementsByTagName("parent");
                    for(int j = 0; j < cls.getLength(); j++){
                        Element cel = (Element)cls.item(j);
                        String pRef = cel.getAttribute("ref");
                        AbstractNode parentNode = getElement(pRef);
                        childNode.addParent(parentNode);
                        parentNode.addChild(childNode);
                    }
                }
            }
            
            
            System.out.println("Parsing XML file is done");
            //new DFSParser(jobMap, doc).run();
            if(options.getAlgorithm().equals("backward")){
                new Backward(jobMap,  doc, options).run();
            }else if(options.getAlgorithm().equals("dfs")){
                new DFSParser(jobMap,   doc, options).run();
            }else if(options.getAlgorithm().equals("bfs")){
                new BFSParser(jobMap,  doc, options).run();
            }else
            {
                printVersion();
                throw new RuntimeException("Incorrect option or option usage " );
            }
            
            


            TransformerFactory tFactory =
            TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            File file = new File(options.getOutputDAX());
            //File file = new File("/Users/chenweiwei/Downloads/store/montage/dags/newdag.xml");
            //File file = new File("/Users/chenweiwei/NetBeansProjects/Priorities/examples/montage/4degree/newdag.xml");
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result); 
        }catch (ParserConfigurationException pce){
            pce.printStackTrace();
            
        }catch (SAXException se){
            se.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }catch (TransformerConfigurationException tce){
            tce.printStackTrace();
        }catch (TransformerException te){
            te.printStackTrace();
        }
    }
    
    
    
    public ConverterOptions parseCommandLineArguments( String[] args ){
        

        //store the args with which converter was invoked
        
        ConverterOptions options = new ConverterOptions();
        options.setOriginalArgString( args );
        
        
        int i = 0;
        while( i < args.length){
            
            switch (args[i].charAt(1)) {

                case 'i'://input dax
                    options.setInputDAX(args[++i]);
                    break;
                
                case 'o'://output dax
                    options.setOutputDAX(args[++i]);
                    break;
                case 'a':
                    options.setAlgorithm(args[++i]);
                    break;
                case 'r':
                    options.setReverse(true);
                    break;
                case 'd':
                    options.setDepth(Integer.parseInt(args[++i]));
                    break;
                case 'm':
                    options.setJobMapMode(args[++i]);
                    break;
                case 'p':
                    options.setPriority(args[++i]);
                    break;
                case 'v':
                    System.out.println("Version " + options.getVersion());
                    break;
                case 'h':
                
                default: //same as help
                    printVersion();
                    System.exit(1);
                    

            }
            i++;
        }

        if(options.checkParams())
        {
            //that is fine
        }
        else{
            throw new RuntimeException("Incorrect option or option usage " );
            
        }
        return options;

    }
    
    private void printVersion(){
                String text =
          "\n $Id: Main.java 4797 2012-06-05 23:42:48Z Weiwei $ " +
          "\n Usage : dax-converter  -i <dax file> -o <dax file> -a algorithm" +
          " [-r reverse] -[d depth]";

        System.out.println(text);
    }
    
}
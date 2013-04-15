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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class Reducer {
    
    
    //private  Map<String, AbstractNode> jobMap;
    private List<String> jobList;
    public Reducer()
    {
        //jobMap = new HashMap<String, AbstractNode>();
        jobList = new ArrayList<String>();
    }
    
   

    private void parseTxtFile(){
        try{
            String filePath = "/Users/chenweiwei/NetBeansProjects/CloudSim3/runtime.txt";
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String line ;
            while((line = in.readLine())!=null){
                String items[] = line.split(" ");
                if(items[0]!=null && items[0].contains("ID")){
                    jobList.add(items[0]);
                }
            }
            System.out.println("Size:" + jobList.size());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    } 
    public void execute(String[] args){
        ConverterOptions options = parseCommandLineArguments(args);
        parseTxtFile();
        parseXmlFile(options);
    }
    public void parseXmlFile(ConverterOptions options){
        DocumentBuilderFactory dbf  = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder db =dbf.newDocumentBuilder();
            //Document doc = db.parse(options.getInputDAX());
            //Document doc = db.parse("/Users/chenweiwei/Downloads/store/montage/dags/dag.xml");
            Document doc = db.parse("/Users/chenweiwei/NetBeansProjects/CloudSim3/dag.xml");
            Element docEle= doc.getDocumentElement();
            NodeList nl = docEle.getElementsByTagName("job");
            
            if(nl != null && nl.getLength() > 0){
                for(int i = 0; i < nl.getLength(); i ++){
                    Element el = (Element)nl.item(i);
                    String id = el.getAttribute("id");
                    if(!jobList.contains(id)){
                        System.out.println("Removing job id " + id);
                        docEle.removeChild(el);
                        //A huge bug here
                        i--;
                    }

                }
            }
            NodeList cl = docEle.getElementsByTagName("child");
            if(cl!=null && cl.getLength()>0){
                for (int i = 0; i < cl.getLength(); i++){
                    Element el = (Element)cl.item(i);
                    String cRef = el.getAttribute("ref");
                    if(!jobList.contains(cRef)){
                        System.out.println("Removing child rf " + cRef);
                        docEle.removeChild(el);
                    }else{
                        NodeList cls = el.getElementsByTagName("parent");
                        for(int j = 0; j < cls.getLength(); j++){
                            Element cel = (Element)cls.item(j);
                            String pRef = cel.getAttribute("ref");
                            if(!jobList.contains(pRef)){
                                System.out.println("Error " + cRef + " and " + pRef);
                            }
                       }
                    }

                }
            }
           



            TransformerFactory tFactory =
            TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            //File file = new File(options.getOutputDAX());
            //File file = new File("/Users/chenweiwei/Downloads/store/montage/dags/newdag.xml");
            File file = new File("/Users/chenweiwei/NetBeansProjects/CloudSim3/newdag.xml");
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
              
                case 'h':
                
                default: //same as help
                    printVersion();
                    

            }
            i++;
        }

//        if(options.checkParams())
//        {
//            //that is fine
//        }
//        else{
//            throw new RuntimeException("Incorrect option or option usage " );
//        }
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

import java.io.File;
import java.io.IOException;
import java.util.*;

import string.StringAnalysis;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;


import soot.Scene;
import soot.ValueBox;


import exceptions.ApplicationPropertiesException;
import exceptions.AnalysisPropertiesException;

import config.AnalysisProperties;
import config.WAMConfiguration;



public class Main {

    //The function to be called, must be static
    public static final String reportFunction = "report";

    //The hotspot signatures to look for in Soot and bcel format
    public static String[] sootSigs = {
            "<java.sql.Statement: boolean execute(java.lang.String)>",
            "<java.sql.Statement: java.sql.ResultSet executeQuery(java.lang.String)>",
            "<java.sql.Statement: int executeUpdate(java.lang.String)>"};

    public static String[] bcelSigs = {
            "java.sql.Statement.execute(Ljava/lang/String;)Z",
            "java.sql.Statement.executeQuery(Ljava/lang/String;)Ljava/sql/ResultSet;",
            "java.sql.Statement.executeUpdate(Ljava/lang/String;)I"};

    private static int instrHotspotCount = 0, optHotspotCount = 0, totalHotspotCount = 0;


    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Correct usage: <propertyfile> <configFile> <classPath>");
            System.exit(-1);
        }

        File propertyFile = new File(args[0]);
        File configFile = new File(args[1]);
        File classPath = new File(args[2]);

        try {
            AnalysisProperties properties = new AnalysisProperties(propertyFile.getCanonicalPath());
            WAMConfiguration application = WAMConfiguration.load(configFile.getAbsolutePath());

            String appName = application.getAppName();

            String runtimeMonitor = properties.getProperty("instrument");

            System.out.println("Analysis of " + appName );
            System.out.println("\tAnalysis property file: " + propertyFile.getAbsolutePath());
            System.out.println("\tApplication property file: " + configFile.getAbsolutePath());
            System.out.println("\tClass path: " + classPath);


            String cp=Scene.v().getSootClassPath()+File.pathSeparator+classPath;
            Scene.v().setSootClassPath(cp);


            Collection<String> classesToAnalyze=application.getClasses();
            System.out.print("\tNumber of classes loaded: ");
            for (String classname:classesToAnalyze) {
                StringAnalysis.loadClass(classname);
            }
            System.out.println(classesToAnalyze.size());

            System.out.print("\tHotspot signatures: ");
            Set<ValueBox> hotspots = new HashSet<ValueBox>();
            for (String sig:sootSigs) {
                hotspots.addAll(StringAnalysis.getExps(sig, 0));
            }
            System.out.println(sootSigs.length);


            System.out.print("Instrumenting classes...");
            for (String c:classesToAnalyze) {
                String filePath = classPath+File.separator+c.replace('.', File.separatorChar)+".class";
                try {
                    JavaClass origClass = (new ClassParser(filePath)).parse();
                    JavaClass newClass = simpleTransform(origClass, appName, runtimeMonitor);
                    newClass.dump(filePath);
                } catch (ClassFormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("done!");

            System.out.println("\tInstrumented points: " + instrHotspotCount);
            System.out.println("\tTotal hotspots: " + totalHotspotCount);
            if (totalHotspotCount != hotspots.size()) {
                System.err.println("BCEL and Soot found a different amount of hotspots! That's really weird...");
            }

            System.out.println("Analysis completed.");

        } catch (ApplicationPropertiesException ape) {
            System.out.println("Could not load application property file: " + configFile.getAbsolutePath());
            ape.printStackTrace();
        } catch (AnalysisPropertiesException ape) {
            System.out.println("Could not load analysis property file: " + ape.getFileLocation());
            ape.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public static JavaClass simpleTransform(JavaClass jc, String appName, String runtimeMonitor) {
        try {
            ClassGen cg = new ClassGen(jc);
            ConstantPoolGen cpg = cg.getConstantPool();
            InstructionFactory instrFactory = new InstructionFactory(cg);

            for (Method m:cg.getMethods()) {
                MethodGen mg = new MethodGen(m, jc.getClassName(), cpg);
//                LineNumberTable lineNumberTable = mg.getLineNumberTable(cpg);
                InstructionList il = mg.getInstructionList();

                if (il != null) {
                    for (InstructionHandle currHandle:il.getInstructionHandles()) {
                        Instruction currInstr = currHandle.getInstruction();
                        if (currInstr instanceof InvokeInstruction) {
                            InvokeInstruction instr = (InvokeInstruction) currInstr;
                            String signature=instr.getClassName(cpg) + "." + instr.getMethodName(cpg) + instr.getSignature(cpg);
                            ArrayList<String> dbSigList = new ArrayList<String>(Arrays.asList(bcelSigs));
                            if (dbSigList.contains(signature)) {
                                totalHotspotCount++;
                                InstructionList checkerInstructions = new InstructionList();
                                checkerInstructions.append(InstructionConstants.DUP);
                                checkerInstructions.append(instrFactory.createInvoke(runtimeMonitor, reportFunction, Type.VOID, new Type[] { Type.STRING}, Constants.INVOKESTATIC));
                                il.insert(currHandle, checkerInstructions);
                                instrHotspotCount++;

                            }
                        }
                    }
                    mg.setMaxStack();
                    mg.setMaxLocals();

                    cg.replaceMethod(m, mg.getMethod());
                }
            }
            return cg.getJavaClass();
        } catch (ClassFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}

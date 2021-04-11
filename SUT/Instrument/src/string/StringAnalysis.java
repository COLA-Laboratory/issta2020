package string;

import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.toolkits.graph.CompleteUnitGraph;

import java.util.*;

public class StringAnalysis{
    //XXX: added by GJ
//    private Graph graph;
//    private Grammar grammar;

    // Make sure we get line numbers
    static {
        //soot.Scene.v().loadNecessaryClasses();
        soot.Scene.v().loadBasicClasses();
        soot.options.Options.v().parse(new String[] { "-keep-line-number" });
    }



    /** Loads the named class into the Soot scene,
     *  marks it as an application class, and generates bodies
     *  for all of its concrete methods.
     *  @param name the fully qualified name of the class to be loaded.
     */
    public static void loadClass(String name) {
        SootClass c = Scene.v().loadClassAndSupport(name);
        c.setApplicationClass();
        Iterator mi = c.getMethods().iterator();
        while (mi.hasNext()) {
            SootMethod sm = (SootMethod)mi.next();
            if (sm.isConcrete()) {
                sm.retrieveActiveBody();
            }
        }
    }



    /** Returns a list containing all expressions occurring as
     *  argument to the specified method.
     It is not checked that these have valid types.
     */
    public static List getExps(String sig, int argnum) {
        ArrayList list = new ArrayList();
        Iterator aci = Scene.v().getApplicationClasses().iterator();
        while (aci.hasNext()) {
            SootClass ac = (SootClass)aci.next();
            Iterator mi = ac.getMethods().iterator();
            while (mi.hasNext()) {
                SootMethod sm = (SootMethod)mi.next();
                if (sm.isConcrete()) {
                    CompleteUnitGraph cug = new CompleteUnitGraph(sm.retrieveActiveBody());

                    Iterator si = cug.iterator();
                    while (si.hasNext()) {
                        Stmt stmt = (Stmt)si.next();
                        if (stmt.containsInvokeExpr()) {
                            InvokeExpr expr = (InvokeExpr)stmt.getInvokeExpr();
                            if (expr.getMethod().getSignature().equals(sig)) {
                                ValueBox box = expr.getArgBox(argnum);
                                list.add(box);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }


}
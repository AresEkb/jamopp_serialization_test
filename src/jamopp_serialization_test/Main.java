package jamopp_serialization_test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.m2m.qvt.oml.BasicModelExtent;
import org.eclipse.m2m.qvt.oml.ExecutionContextImpl;
import org.eclipse.m2m.qvt.oml.ExecutionDiagnostic;
import org.eclipse.m2m.qvt.oml.ModelExtent;
import org.eclipse.m2m.qvt.oml.TransformationExecutor;
import org.eclipse.m2m.qvt.oml.util.WriterLog;
import org.emftext.language.java.resource.JaMoPPUtil;

public class Main {
	
    public static void main(String[] args) {
    	final String transformation = "transforms/NewTransformation.qvto";
    	final URI transformationURI = URI.createFileURI(new File(transformation).getAbsolutePath());
    	final String output = "output/test_stand_alone.java";
    	final URI outputURI = URI.createFileURI(new File(output).getAbsolutePath());
    	final String lang = "lib/java/lang/package-info.java"; //file needs to exist with content 'package java.lang;'
    	final URI langURI = URI.createFileURI(new File(lang).getAbsolutePath());
    	
        ResourceSet rs = new ResourceSetImpl();
        JaMoPPUtil.initialize();
    	
    	TransformationExecutor executor = new TransformationExecutor(transformationURI);
	    ExecutionContextImpl context = new ExecutionContextImpl();
	    context.setConfigProperty("keepModeling", true);
	    context.setLog(new WriterLog(new OutputStreamWriter(System.out)));
	    ModelExtent inLang = new BasicModelExtent(rs.getResource(langURI, true).getContents());
	    ModelExtent outputModel = new BasicModelExtent();
	    ExecutionDiagnostic result = executor.execute(context, inLang, outputModel);
	    
	    if(result.getSeverity() == Diagnostic.OK) {
	        try {
		        Resource res = rs.createResource(outputURI);
		        res.getContents().addAll(outputModel.getContents());
		        Map<Object, Object> options = new HashMap<Object, Object>();
				res.save(options);
		        res.unload();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    
	    } else {
	        IStatus status = BasicDiagnostic.toIStatus(result);
	        for (IStatus error : status.getChildren()) {
	            System.err.println("  " + error);
	        }
	    }
    }
}

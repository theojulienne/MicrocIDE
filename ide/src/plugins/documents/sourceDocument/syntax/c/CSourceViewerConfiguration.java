package plugins.documents.sourceDocument.syntax.c;

import org.eclipse.jface.text.IDocument;
/*
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
*/
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import plugins.documents.sourceDocument.SourceDocument;

public class CSourceViewerConfiguration extends SourceViewerConfiguration {
	private SourceDocument document;

	public CSourceViewerConfiguration( SourceDocument document ) {
		this.document = document;
	}
	
	public IPresentationReconciler getPresentationReconciler( ISourceViewer sourceViewer ) {
		PresentationReconciler pr = new PresentationReconciler();
		
		DefaultDamagerRepairer ddr = new DefaultDamagerRepairer( new CCommentBlockScanner( document ) );
	    pr.setDamager( ddr, CPartitionScanner.C_MULTILINE_COMMENT );
	    pr.setRepairer( ddr, CPartitionScanner.C_MULTILINE_COMMENT );

		ddr = new DefaultDamagerRepairer( new CDocCommentScanner( document ) );
	    pr.setDamager( ddr, CPartitionScanner.C_DOCUMENTATION_COMMENT );
	    pr.setRepairer( ddr, CPartitionScanner.C_DOCUMENTATION_COMMENT );

		ddr = new DefaultDamagerRepairer( new CDirectiveScanner( document ) );
	    pr.setDamager( ddr, CPartitionScanner.C_COMPILER_DIRECTIVE );
	    pr.setRepairer( ddr, CPartitionScanner.C_COMPILER_DIRECTIVE );
	    

		ddr = new DefaultDamagerRepairer( new CStringScanner( document ) );
	    pr.setDamager( ddr, CPartitionScanner.C_STRING_LITERAL );
	    pr.setRepairer( ddr, CPartitionScanner.C_STRING_LITERAL );
	    
		ddr = new DefaultDamagerRepairer( new CScanner( document ) );
		pr.setRepairer(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		pr.setDamager(ddr, IDocument.DEFAULT_CONTENT_TYPE);
		
		return pr;
	}
	 
	// autocomplete:
	/*
	IContentAssistant getContentAssistant( ISourceViewer sv ) {
		ContentAssistant ca = new ContentAssistant();
		IContentAssistProcessor cap = new TestCompletionProcessor(); // TODO
		ca.setContentAssistProcessor(cap, IDocument.DEFAULT_CONTENT_TYPE);
		ca.setInformationControlCreator(getInformationControlCreator(sv));
		return ca;
	}
	*/
	 
	// hover handler:
	/*
	public ITextHover getTextHover(ISourceViewer sv, String contentType) {
		return new TestTextHover(); // TODO
	}
	*/
}

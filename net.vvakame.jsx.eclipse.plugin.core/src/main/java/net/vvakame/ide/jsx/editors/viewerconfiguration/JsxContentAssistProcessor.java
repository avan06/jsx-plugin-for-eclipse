package net.vvakame.ide.jsx.editors.viewerconfiguration;

import java.util.List;

import net.vvakame.ide.jsx.Activator;
import net.vvakame.ide.jsx.editors.JsxEditor;
import net.vvakame.jsx.wrapper.Jsx;
import net.vvakame.jsx.wrapper.Jsx.Builder;
import net.vvakame.jsx.wrapper.JsxCommandException;
import net.vvakame.jsx.wrapper.completeentity.Complete;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * Code assist for JSX editor. powered by JSX with --complete option.
 * @author vvakame
 */
public class JsxContentAssistProcessor implements IContentAssistProcessor {

	JsxEditor editor;


	/**
	 * the constructor.
	 * @param editor
	 * @category constructor
	 */
	public JsxContentAssistProcessor(JsxEditor editor) {
		this.editor = editor;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
		String jsxCodePath = file.getRawLocation().makeAbsolute().toFile().getAbsolutePath();

		Builder builder = Activator.getDefault().getJsxCommandBuilder();

		IDocument document = viewer.getDocument();
		int lineIndex = -1;
		int columnIndex = -1;
		try {
			lineIndex = document.getLineOfOffset(offset);
			columnIndex = offset - document.getLineOffset(lineIndex);

			lineIndex += 1;
			columnIndex += 1;
		} catch (BadLocationException e) {
			Activator.getDefault().getLog()
				.log(new Status(Status.ERROR, Activator.PLUGIN_ID, "raise error", e));
			return null;
		}

		List<Complete> completeList = null;
		try {
			builder.jsxSource(jsxCodePath);
			if (!editor.isDirty()) {
				completeList = Jsx.getInstance().complete(builder.build(), lineIndex, columnIndex);
			} else {
				completeList =
						Jsx.getInstance().complete(builder.build(), document.get(), lineIndex,
								columnIndex);
			}
		} catch (JsxCommandException e) {
			Activator.getDefault().getLog()
				.log(new Status(Status.ERROR, Activator.PLUGIN_ID, "raise error", e));
			return null;
		}

		ICompletionProposal[] result = new ICompletionProposal[completeList.size()];

		for (int i = 0; i < completeList.size(); i++) {
			Complete complete = completeList.get(i);

			final String replacementString;
			if (complete.getPartialWord() != null) {
				replacementString = complete.getPartialWord();
			} else {
				replacementString = complete.getWord();
			}
			final String displayString = complete.getWord();
			final String additionalProposalInfo = complete.getDoc();

			result[i] =
					new CompletionProposal(replacementString, offset, 0,
							replacementString.length(), null, displayString, null,
							additionalProposalInfo);
		}

		return result;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}
}

package com.widgetexperiments.client;

import java.util.LinkedList;
import java.util.Queue;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetExperiments implements EntryPoint {

	private VerticalPanel mainPanel = new VerticalPanel();
	private Tree tree = new Tree();
	private Label label = new Label();

	public void onModuleLoad() {

		tree.addItem(new Proposition());

		label.setText("no message");

		// Assemble Main panel.
		mainPanel.add(label);
		mainPanel.add(tree);

		// Associate the Main panel with the HTML host page.
		RootPanel.get("widgetExperiments").add(mainPanel);

	}

	private static class Proposition extends TreeItem implements ClickHandler,
			KeyPressHandler, FocusHandler {
		// TODO: synchronize arguments with server

		private static Proposition lastPropositionWithFocus = null;
		private TextAreaSloppyGrow textArea = new TextAreaSloppyGrow();
		private Button proButton = new Button("For");
		private Button conButton = new Button("Against");
		private TreeItem parentArgument;

		public Proposition(TreeItem parentArgument) {
			this();
			this.parentArgument = parentArgument;
		}

		public Proposition() {
			super();
			VerticalPanel verticalPanel = new VerticalPanel();
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			verticalPanel.add(textArea);
			verticalPanel.add(horizontalPanel);
			horizontalPanel.add(proButton);
			horizontalPanel.add(conButton);
			this.setWidget(verticalPanel);

			proButton.addClickHandler(this);
			conButton.addClickHandler(this);

			textArea.addKeyPressHandler(this);
			textArea.addFocusHandler(this);
			//textArea.addBlurHandler(this);
			proButton.addFocusHandler( this );
			//proButton.addBlurHandler( this);
			conButton.addFocusHandler( this );
			//conButton.addBlurHandler( this );
			setState(true);
		}

		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource() == proButton) {
				addArgument("Argument For");

			} else if (event.getSource() == conButton) {
				addArgument("Argument Against");
			}
		}

		public void addArgument(String labelText) {
			TreeItem argumentTreeItem = new TreeItem(labelText);
			argumentTreeItem.addItem(new Proposition(argumentTreeItem));
			this.addItem(argumentTreeItem);
			argumentTreeItem.setState(true);
			this.setState(true);
		}

		@Override
		public void onKeyPress(KeyPressEvent event) {
			char charCode = event.getCharCode();
			Object source = event.getSource();
			String textAreaContent = textArea.getText();
			if (source == textArea && (charCode == '\n' || charCode == '\r')
					&& parentArgument != null) {
				Proposition newProposition = new Proposition(parentArgument);

				/*
				 * can't figure out how to insert an item at a specific point
				 * (instead items just get inserted as the last of the current
				 * TreeItem's children). So, instead, I'm removing all
				 * subsequent TreeItem children, then adding the new TreeItem
				 * (the new proposition) and then adding back all the subsequent
				 * tree items!
				 */

				// first remove subsequent children
				int treePosition = parentArgument.getChildIndex(this);
				Queue<TreeItem> removeQueue = new LinkedList<TreeItem>();
				TreeItem currentItem;
				while ((currentItem = parentArgument.getChild(treePosition + 1)) != null) {
					removeQueue.add(currentItem);
					parentArgument.removeItem(currentItem);
				}

				// then add the new one
				parentArgument.addItem(newProposition);

				// then add back the rest
				while (!removeQueue.isEmpty()) {
					TreeItem toRemove = removeQueue.poll();
					parentArgument.addItem(toRemove);
				}

				newProposition.textArea.setFocus(true);
			} else if (source == textArea && (charCode == '\b')
					&& parentArgument != null && textAreaContent.equals("")) {
				if (parentArgument.getChildCount() > 1) {
					parentArgument.removeItem(this);
				} else {
					parentArgument.remove();
				}
			}
		}

		@Override
		public void onFocus(FocusEvent event) {
			Object source = event.getSource();
			if( source == textArea ){
				if( lastPropositionWithFocus != this ){
					lastPropositionWithFocus.proButton.setVisible(false);
					lastPropositionWithFocus.conButton.setVisible(false);
					lastPropositionWithFocus = this;
				}
				proButton.setVisible(true);
				conButton.setVisible(true);
			}
		}
			/*
			if( source == proButton ||
					event.getSource() == conButton ){
				buttonHasFocus = true;
			} else if (source == textArea ){
				proButton.setVisible(true);
				conButton.setVisible(true);
			}
		}*/
/*
		@Override
		public void onBlur(BlurEvent event) {
			Object source = event.getSource();
			if( source == proButton ||
					event.getSource() == conButton ){
				buttonHasFocus = false;
			} else if (source == textArea &&
					buttonHasFocus == false ){
				proButton.setVisible(false);
				conButton.setVisible(false);
			}
		}*/

	}

	private static class TextAreaSloppyGrow extends TextArea {
		public TextAreaSloppyGrow() {
			this(80);
		}

		public TextAreaSloppyGrow(int width) {
			super();

			this.setCharacterWidth(width);
			setVisibleLines(1);

			this.addKeyPressHandler(new KeyPressHandler() {
				public void onKeyPress(KeyPressEvent event) {
					TextArea source = (TextArea) event.getSource();
					int widthInCharacters = source.getCharacterWidth();
					String text = source.getText();
					int length = text.length();

					int newLineCount = 0;
					for (int i = 0; i < length; i++) {
						if (text.charAt(i) == '\n') {
							newLineCount++;
						}
					}
					char charCode = event.getCharCode();
					if (charCode == '\n' || charCode == '\r') {
						newLineCount++;
					}

					int lineEstimate = length / widthInCharacters
							+ newLineCount;
					if (lineEstimate < 1) {
						lineEstimate = 1;
					}
					source.setVisibleLines(lineEstimate);
					// source.setVisibleLines( 30 );
				}
			});
		}
	}
}

/*
 * looking for a way to make textArea respond to copy paste, and basically any
 * changes to the text, instead of just keypresses. The below, didn't seem to
 * work:
 * 
 * 
 * textArea.addValueChangeHandler(new ValueChangeHandler<String>() {
 * 
 * @Override public void onValueChange(ValueChangeEvent<String> event) {
 * TextArea source = (TextArea) event.getSource(); int widthInCharacters =
 * source.getCharacterWidth(); String text = source.getText(); int length =
 * text.length();
 * 
 * int newLineCount = 0; for( int i=0; i<length; i++){ if( text.charAt( i ) ==
 * '\n' ){ newLineCount++; } }
 * 
 * int lineEstimate = length/widthInCharacters + newLineCount;
 * source.setVisibleLines( lineEstimate ); label.setText(newLineCount +
 * " new lines" + "; " + lineEstimate + " line estimate; " + text + " = text");
 * 
 * } });
 */

/*
 * incorporated into new proposition (but this shows how to use a composite...)
 * private static class Proposition extends Composite {
 * 
 * private TextAreaSloppyGrow textArea = new TextAreaSloppyGrow(); private
 * Button clarifyButton = new Button( "Clarify" ); private Button proButton =
 * new Button( "For" ); private Button conButton = new Button( "Against" );
 * 
 * 
 * public Proposition() { VerticalPanel verticalPanel = new VerticalPanel();
 * HorizontalPanel horizontalPanel = new HorizontalPanel();
 * verticalPanel.add(textArea); verticalPanel.add(horizontalPanel);
 * horizontalPanel.add( clarifyButton ); horizontalPanel.add(proButton);
 * horizontalPanel.add(conButton);
 * 
 * // All composites must call initWidget() in their constructors.
 * initWidget(verticalPanel);
 * 
 * // Give the overall composite a style name.
 * setStyleName("example-OptionalCheckBox"); } }
 */

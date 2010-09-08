package com.widgetexperiments.client;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
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
	//test git
	private VerticalPanel mainPanel = new VerticalPanel();
	private Tree tree = new Tree();
	private Label label = new Label();
	
	public void onModuleLoad() {
	    
	    tree.addItem( new Proposition() );
	    
	    label.setText("no message");
	    

	    // Assemble Main panel.
	    mainPanel.add(label);
	    mainPanel.add(tree);

	    // Associate the Main panel with the HTML host page.
	    RootPanel.get("widgetExperiments").add(mainPanel);
	    
	}
	
	private static class Proposition extends TreeItem implements ClickHandler {
		//TODO: hide buttons when without focus
		//TODO: limit make text uneditable when not "clarifying"
		//TODO: synchronize arguments with server
		//TODO: distinguish between arguments and propositions
		
		private TextAreaSloppyGrow textArea = new TextAreaSloppyGrow();
		private Button clarifyButton = new Button( "Clarify" );
		private Button proButton = new Button( "For" );
		private Button conButton = new Button( "Against" );
		
		
		public Proposition() {
			super();
			VerticalPanel verticalPanel = new VerticalPanel();
			HorizontalPanel horizontalPanel = new HorizontalPanel();
			verticalPanel.add(textArea);
			verticalPanel.add(horizontalPanel);
			horizontalPanel.add(clarifyButton);
			horizontalPanel.add(proButton);
			horizontalPanel.add(conButton);
			this.addItem( verticalPanel );
			
			proButton.addClickHandler( this );
			conButton.addClickHandler( this );
			clarifyButton.addClickHandler( this );
		}


		@Override
		public void onClick(ClickEvent event) {
			if( event.getSource() == clarifyButton ){
				return;
			}
			else if( event.getSource() == proButton ){
				this.addItem( new Proposition() );
			}
			else if( event.getSource() == conButton ){
				this.addItem( new Proposition() );
			}
			
		}
	}
	 
	private static class TextAreaSloppyGrow extends TextArea{
		public TextAreaSloppyGrow () {
			this( 80 );
		}
		 public TextAreaSloppyGrow( int width ){
			 super();
			 
			 this.setCharacterWidth(width);
			 setVisibleLines( 1 );
			 
			 this.addKeyPressHandler( new KeyPressHandler() {
		    	public void onKeyPress( KeyPressEvent event){
		    		TextArea source = (TextArea) event.getSource();
		    		int widthInCharacters = source.getCharacterWidth();
		    		String text = source.getText();
		    		int length = text.length();
		    		
		    		int newLineCount = 0;
		    		for( int i=0; i<length; i++){
		    			if( text.charAt( i ) == '\n' ){
		    				newLineCount++;	
		    			}
		    		}
		    		char charCode = event.getCharCode();
		    		if( charCode == '\n' || charCode == '\r') {
		    			newLineCount++;
		    		}
		    		
		    		int lineEstimate = length/widthInCharacters + newLineCount;
		    		if( lineEstimate <1 ){
		    			lineEstimate = 1;
		    		}
		    		source.setVisibleLines( lineEstimate );
		    		//source.setVisibleLines( 30 );
		    	}
		    });
		 }
	 }
}

/*
looking for a way to make textArea respond to copy paste, and basically
any changes to the text, instead of just keypresses.  The below, didn't seem to work:


textArea.addValueChangeHandler(new ValueChangeHandler<String>() {

@Override
public void onValueChange(ValueChangeEvent<String> event) {
	TextArea source = (TextArea) event.getSource();
	int widthInCharacters = source.getCharacterWidth();
	String text = source.getText();
	int length = text.length();
	
	int newLineCount = 0;
	for( int i=0; i<length; i++){
		if( text.charAt( i ) == '\n' ){
			newLineCount++;	
		}
	}
	
	int lineEstimate = length/widthInCharacters + newLineCount;
	source.setVisibleLines( lineEstimate );
	label.setText(newLineCount + " new lines" + "; " + lineEstimate + " line estimate; " + text + " = text");
	
}
});*/

/* incorporated into new proposition (but this shows how to use a composite...)
private static class Proposition extends Composite {
	
	private TextAreaSloppyGrow textArea = new TextAreaSloppyGrow();
	private Button clarifyButton = new Button( "Clarify" );
	private Button proButton = new Button( "For" );
	private Button conButton = new Button( "Against" );
	
	
	public Proposition() {
		VerticalPanel verticalPanel = new VerticalPanel();
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(textArea);
		verticalPanel.add(horizontalPanel);
		horizontalPanel.add( clarifyButton );
		horizontalPanel.add(proButton);
		horizontalPanel.add(conButton);
		
		// All composites must call initWidget() in their constructors.
		initWidget(verticalPanel);
		
		// Give the overall composite a style name.
		setStyleName("example-OptionalCheckBox");
	}
}
*/

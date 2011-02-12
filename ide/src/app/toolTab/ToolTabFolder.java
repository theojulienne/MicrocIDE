package app.toolTab;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.json.JSONObject;

import app.Application;
import app.plugin.PluginManager;
import app.plugin.base.PluginTool;
import app.plugin.interfaces.IMenuBar;
import app.plugin.interfaces.IPlugin;
import app.plugin.interfaces.IToolBar;
import app.plugin.interfaces.parents.IToolParent;
import app.project.ProjectWindow;
import app.document.DocumentTab;

public class ToolTabFolder extends CTabFolder implements IToolParent {

	private ProjectWindow project;

	private HashMap<PluginTool, ToolTab> tabs;

	public ToolTabFolder( Composite parent, int style ) {
		super( parent, style );
		
		this.setSimple( true );
		this.setMaximizeVisible( true );
		this.setMinimizeVisible( true );
		
		this.tabs = new HashMap<PluginTool, ToolTab>( );
		
		addListeners( );
		
		this.setSelection( 0 );
	}
	
	public void setProjectWindow( ProjectWindow project ) {
		this.project = project;
	}
	
	public Collection<PluginTool> getTools( ) {
		return tabs.keySet();
	}
	
	private void addListeners( ) {
		this.addCTabFolder2Listener( new CTabFolder2Listener() {

		      public void close( CTabFolderEvent evt ) {
		      }

		      public void minimize( CTabFolderEvent evt ) {

		    	  CTabFolder folder = (CTabFolder) evt.widget;
		    	  SashForm parent = (SashForm) folder.getParent();
		    	  
		    	  if ( !folder.getMaximized() && !folder.getMinimized() ) {
		    		  folder.setData( parent.getWeights() );
		    	  }
		    	  
		    	  
		    	  parent.setMaximizedControl( null );
		    	  
		    	  int heightRatio = parent.getClientArea().height / (folder.getTabHeight() + 5);
		    	  System.out.println( heightRatio );
		    	  parent.setWeights( new int[]{ heightRatio, 1 } );
		    	  
		    	  folder.setMinimized( true );
		      }

		      public void maximize( CTabFolderEvent evt ) {
		    	  CTabFolder folder = (CTabFolder) evt.widget;
		    	  SashForm parent = (SashForm) folder.getParent();
		    	  
		    	  if ( !folder.getMaximized() && !folder.getMinimized() ) {
		    		  folder.setData( parent.getWeights() );
		    	  }
		    	  
		    	  folder.setMaximized( true );
		    	  parent.setWeights( new int[]{ 1, 10 } );
		    	  parent.setMaximizedControl( folder );
		      }

		      public void restore( CTabFolderEvent evt ) {
		    	  CTabFolder folder = (CTabFolder) evt.widget;
		    	  folder.setMaximized( false );
		    	  folder.setMinimized( false );
		    	  SashForm parent = (SashForm) folder.getParent();
		    	  
		    	  if ( folder.getData() != null ) {
		    		  parent.setWeights( (int[]) folder.getData() );
		    	  } else {
		    		  parent.setWeights( new int[]{ 7, 3 } );
		    	  }
		    	  
		    	  parent.setMaximizedControl( null );
		      }

		      public void showList(CTabFolderEvent evt) {
		      }
		} );
		
		this.addControlListener( new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}
			public void controlResized(ControlEvent e) {
				CTabFolder folder = (CTabFolder) e.widget;
				folder.setMinimized( false );
			}
		} );
	}
	
	public Composite getComposite() {
		return this;
	}

	public File getProjectPath() {
		return project.getPath();
	}
	
	public void createTools( ) {
		// TODO: dynamic tool loading when plugins changed
		for ( IPlugin plugin : Application.getInstance().getPluginManager().listLoadedPlugins() ) {
			if ( plugin.hasTool() ) {
				PluginTool tool = plugin.createTool( this );
				if ( tool != null ) {
					this.tabs.put( tool, new ToolTab( this, tool ) );
				}
			}
		}
	}

	public void openDocument( File documentFile ) {
		project.openDocument( documentFile );
	}

	public void updateFiles() {
		project.updateTree( );
	}

	public IToolBar getToolBar() {
		return project.getToolBar( );
	}

	public IMenuBar getMenuBar() {
		return project.getMenuBar( );
	}

	public void updateIcon( PluginTool tool ) {
		if ( tabs.containsKey( tool ) ) {
			tabs.get( tool ).updateIcon( );
		}
	}

	public void setDocumentEnabled( DocumentTab focussedDocumentTab,
			boolean enabled) {
		
		if ( focussedDocumentTab != null ) {
			for ( PluginTool tool : tabs.keySet() ) {
				tool.setDocumentEnabled( focussedDocumentTab.getDocument(), enabled );
			}
		}
	}

	public void setProjectEnabled( boolean enabled ) {
		for ( PluginTool tool : tabs.keySet() ) {
			tool.setProjectEnabled( enabled );
		}
	}
	
	public JSONObject getPluginProjectSettings( IPlugin plugin ) {
		return PluginManager.getPluginProjectSettings( project.getPath(), plugin );
	}

	public JSONObject getPluginAppSettings(IPlugin plugin) {
		return PluginManager.getPluginAppSettings( plugin );
	}

	public boolean isMac() {
		return Application.isMac( );
	}

	public void setActiveTool( PluginTool tool ) {
		if ( tabs.containsKey( tool ) ) {
			CTabItem currToolTab = tabs.get( tool );
			this.setSelection( currToolTab );
		}
	}

}

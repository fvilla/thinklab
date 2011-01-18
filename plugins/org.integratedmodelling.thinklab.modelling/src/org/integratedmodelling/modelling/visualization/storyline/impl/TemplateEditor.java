package org.integratedmodelling.modelling.visualization.storyline.impl;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TemplateEditor extends ApplicationWindow {

	/**
	 * Create the application window,
	 */
	public TemplateEditor() {
		super(null);
		createActions();
		addCoolBar(SWT.FLAT);
		addMenuBar();
		addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		{
			Composite leftFrame = new Composite(container, SWT.NONE);
			leftFrame.setLayout(new GridLayout(1, false));
			GridData gd_leftFrame = new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1);
			gd_leftFrame.widthHint = 600;
			gd_leftFrame.minimumWidth = 680;
			leftFrame.setLayoutData(gd_leftFrame);
			{
				Composite picBar = new Composite(leftFrame, SWT.NONE);
				picBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
				picBar.setBounds(0, 0, 64, 64);
			}
			{
				Composite image = new Composite(leftFrame, SWT.NONE);
				GridData gd_image = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
				gd_image.minimumWidth = 600;
				gd_image.widthHint = 680;
				gd_image.heightHint = 540;
				image.setLayoutData(gd_image);
				image.setBounds(0, 0, 64, 64);
			}
			{
				Composite composite = new Composite(leftFrame, SWT.NONE);
				composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
				composite.setBounds(0, 0, 64, 64);
			}
		}
		
		Composite rightFrame = new Composite(container, SWT.NONE);
		rightFrame.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the coolbar manager.
	 * @return the coolbar manager
	 */
	@Override
	protected CoolBarManager createCoolBarManager(int style) {
		CoolBarManager coolBarManager = new CoolBarManager(style);
		{
			ToolBarManager toolBarManager = new ToolBarManager();
			coolBarManager.add(toolBarManager);
		}
		return coolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			TemplateEditor window = new TemplateEditor();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("New Application");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(966, 802);
	}
}
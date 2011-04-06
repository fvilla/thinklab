package org.integratedmodelling.modelling.storyline;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.interfaces.IPresentation;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.modelling.visualization.knowledge.TypeManager;
import org.integratedmodelling.modelling.visualization.knowledge.VisualConcept;
import org.integratedmodelling.modelling.visualization.storyline.StorylineTemplate;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Path;

public class StorylineFactory {

	IntelligentMap<StorylineTemplate> _presentations = new IntelligentMap<StorylineTemplate>();
	static StorylineFactory _this = null;
	static ArrayList<File> _directories = new ArrayList<File>();
	static HashMap<File, StorylineTemplate> _cache = new HashMap<File, StorylineTemplate>();
	static HashMap<String, StorylineTemplate> _templatesByID = 
		new HashMap<String, StorylineTemplate>();
	
	public static StorylineTemplate getPresentation(IConcept concept) {
		return get()._presentations.get(concept);
	}

	public static StorylineTemplate createTemplate(String path, IConcept concept) throws ThinklabException {
		
		if (getStoryline(path) != null) {
			throw new ThinklabValidationException(
					"cannot create storyline template " + path + ": storyline exists");
		}
		String pnm = Path.getLast(path, '.');
		String pth = Path.getLeading(path, '.');
		Storyline parent = null;
		if (pth != null)
			parent = getStoryline(pth);
		if (pth == null || parent == null) {
			throw new ThinklabValidationException(
					"storyline template " + 
					path + 
					" does not have a parent: please use a path that names an existing parent");			
		}
		
		File d = new File(parent.getTemplate().getSourceFile().getParent() + File.separator + pnm);
		d.mkdirs();
		File f = new File(d + File.separator + pnm + ".xml");
		
		StorylineTemplate st = new StorylineTemplate();
		
		VisualConcept vc = TypeManager.get().getVisualConcept(concept);
		
		st.addField("id", path, null);
		st.addField("concept", concept.toString(), null);
		st.addField("title", vc.getLabel(), null);
		st.addField("runninghead", vc.getLabel(), null);
		st.addField("description", pnm, null);

		st.setSourceFile(f);
		
		_cache.put(f, st);
		_templatesByID.put(pth, st);
		
		st.write(f.toString());
		
		return st;
	}
	
	public static StorylineTemplate readTemplate(File f) throws ThinklabException {
		
		if (_cache.containsKey(f))
			return _cache.get(f);
		
		StorylineTemplate p = new StorylineTemplate();
		p.setSourceFile(f);
		p.read(f.toString());
		_cache.put(f, p);
		_templatesByID.put(p.getId(), p);
		return p;
		
	}
	
	/**
	 * Save a template back to the original file it was read from, making a
	 * backup copy of the original one.
	 * 
	 * @param f
	 * @return
	 * @throws ThinklabException
	 */
	public static void saveTemplate(StorylineTemplate template) throws ThinklabException {

		File dest = null;
		for (int i = 1; ; i++) {
			dest = new File(
				MiscUtilities.changeExtension(template.getSourceFile().toString(), "bk" + i));
			if (!dest.exists())
				break;
		}
			
		CopyURL.copy(template.getSourceFile(), dest);
		template.write(template.getSourceFile().toString());
	}
	
	public static void writeTemplate(StorylineTemplate template, File f) throws ThinklabException {
		template.write(f.toString());
	}
	
	/**
	 * Return a new storyline for the given path, or null if there is no
	 * template path to define it.
	 * 
	 * @param path
	 * @return
	 * @throws ThinklabException
	 */
	public static Storyline getStoryline(String path) throws ThinklabException {
		List<File> pth = getTemplatePath(path);
		if (pth == null)
			return null;
		return getStoryline(pth, false);
	}
	
	/**
	 * Return a new storyline for the given path, or null if there is no
	 * template path to define it. Add all the child storylines that are found
	 * in the namespace.
	 * 
	 * @param path
	 * @return
	 * @throws ThinklabException
	 */
	public static Storyline getStorylines(String path) throws ThinklabException {
		List<File> pth = getTemplatePath(path);
		if (pth == null)
			return null;
		return getStoryline(pth, true);
	}
	
	public static Storyline createStoryline(File f) throws ThinklabException {
		
		StorylineTemplate template = readTemplate(f);
		Storyline ret = null;
		
		/*
		 * produce the appropriate storyline for the template
		 */
		if (template.getModelSpecifications().size() > 0) {
			ret = new ModelStoryline(template);
		} else {
			ret = new Storyline(template);
		}
		
		return ret;
	}
	
	public static Storyline getStoryline(List<File> templates, boolean getChildren) throws ThinklabException {
		
		Storyline ret = null;
		Storyline prev = null;
		
		for (File f : templates) {
			
			ret = createStoryline(f);
			
			/*
			 * add as a child to previous
			 */
			if (prev != null) {
				prev.add(ret);
			}
			prev = ret;
		}

		// add all child storylines if requested
		if (getChildren && templates.size() > 0) {
			for (Storyline s : getChildStorylines(templates.get(templates.size() - 1), true)) {
				prev.add(s);
			}
		}
		
		return ret;
	}
	
	private static Collection<Storyline> getChildStorylines(File file, boolean isRoot) throws ThinklabException {
		
		/*
		 *  in order to have children, the directory containing the file must have
		 *  subdirectories containing storylines. Sibling .xml files are storylines
		 *  only if not the root directory, not self and not parent.
		 */
		ArrayList<Storyline> ret = new ArrayList<Storyline>();
		String ps = file.getParent();
		if (ps == null)	
			return ret;
		
		File pfile = getStorylineFile(new File(ps));
		
		for (File d : new File(ps).listFiles()) {
			File sf = getStorylineFile(d);
			if (sf != null) {
				Storyline s = createStoryline(sf);
				for (Storyline ss : getChildStorylines(sf, false)) {
					s.add(ss);
				}
				ret.add(s);
			} else if (//!isRoot &&
						d.toString().endsWith(".xml") && 
						!d.equals(file) && 
						!d.equals(pfile)) {
				ret.add(createStoryline(d));
			}
		}
		
		return ret;
	}

	/**
	 * Take a file name and if it contains a file with the directory's own name and 
	 * a storyline file in it, return that file.
	 * @param f
	 * @return
	 */
	private static File getStorylineFile(File f) {

		if (f.isDirectory()) {
			File r  = 
				new File(
					f + 
					File.separator + 
					MiscUtilities.getFileBaseName(f.toString()) + 
					".xml");
			if (r.exists())
				return r;
		}
		
		return null;
		
	}

	public static List<File> getTemplatePath(String namespace) {
		
		List<File> ret = null;
		for (File dir : _directories) {
			ret = findPresentationPath(namespace, dir);
			if (ret != null)
				break;
		}		
		return ret;
	}
	
	private static List<File> findPresentationPath(String path, File dir) {
		
		String[] pth = path.split("\\.");
		
		/*
		 * lookup template in this dir; if there, find the rest first
		 */
		File tp = new File(dir + File.separator + pth[0] + ".xml");
		if (!tp.exists())
			return null;
		
		List<File> ret = new ArrayList<File>();
		ret.add(tp);
		
		if (pth.length > 1) {
			/*
			 * find file in subdir if there is one with the pathname
			 */
			File sdir = new File(dir + File.separator + pth[1]);
			if (sdir.exists() && sdir.isDirectory()) {
				dir = sdir;
			}
			List<File> lf = findPresentationPath(Path.join(pth, 1, '.'), dir);
			if (lf == null)
				return null;
			for (File ff : lf) {
				ret.add(ff);
			}
		}
		
		return ret;
	}
	
	public static synchronized void addSourceDirectory(File dir) throws ThinklabException {
		_directories.add(dir);
		scanDirectory(dir);
	}
		
	public static synchronized void scanDirectory(File dir) throws ThinklabException {
		scanDirectoryInternal(dir, null);
	}
	
	private static void scanDirectoryInternal(File dir, Collection<StorylineTemplate> roots) throws ThinklabException {
		
		if (roots == null)
			roots = new ArrayList<StorylineTemplate>();
		
		for (File f : dir.listFiles()) {
			if (f.toString().endsWith(".xml")) {
				ModellingPlugin.get().logger().info("reading " + f + "...");
				StorylineTemplate p = new StorylineTemplate();
				p.read(f.toString());
				p.setSourceFile(f);
				if (p != null) {
					get()._presentations.put(p.getConcept(), p);
					_cache.put(f, p);
					_templatesByID.put(p.getId(), p);
					ModellingPlugin.get().logger().info("presentation template " + p + " read successfully");
				}
			} 
		}
		
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				scanDirectory(f);
			}
		}
	}

	static StorylineFactory get() {

		if (_this == null) {
			_this = new StorylineFactory();
		}
		return _this;
	}
	
	/**
	 * Return template by ID, provided it's been read.
	 * 
	 * @param inherited
	 * @return
	 */
	public static StorylineTemplate getPresentation(String inherited) {
		return _templatesByID.get(inherited);
	}
}

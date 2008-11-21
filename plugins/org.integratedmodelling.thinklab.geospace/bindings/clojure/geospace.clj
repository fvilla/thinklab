;; -----------------------------------------------------------------------------------------
;; Clojure bindings for the Geospace plugin
;; VERY incomplete, stubs only
;; @author Ferdinando Villa
;; @date Nov 15, 2008
;; -----------------------------------------------------------------------------------------

(ns tl
	(:import
		(org.integratedmodelling.geospace.observations RasterGrid)))

(defn get-boundary
	"Returns a space areal observation with the boundary of the given observation, or the convex hull
	 of a list of observations."
	 []
	 nil)
	 
(defn grid
	"Returns a new grid observation from a shape and a maximum linear resolution. The grid will
	 be set to contain the shape, and the widest dimension will have the given number of cells."
	[where max-linear-resolution]
	(. RasterGrid (createRasterGrid where max-linear-resolution)))
	
(defn areal-location
		"Returns a new areal space observation with the given shape"
	[shape]
	nil)
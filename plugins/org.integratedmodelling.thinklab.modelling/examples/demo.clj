(defmodel demo-biomass-model 'ecology:PlantBiomass
	
	"A sample model of plant biomass showing alternative formulations according to biome"
	
	[(classification 'landuse:CoverClass [0 32] 'Terrestrial [34 :>] 'Aquatic) 
			:cluster-states true 
		  :as biome]
	
	(measurement 'ecology:Biomass "kg/m^2") 
		  :context    time
			:derivative (time:Time '(* self (^ growth-rate time))) 
			:when       (is? biome 'Terrestrial)
			
	(classification (measurement 'ecology:Biomass "kg/m^2") [0 2.5] 'biomass:Low [2.5 :>] 'biomass:High)
	   :context 
	   	 (classification 
	   	 			(measurement 'biophysics:SeaLevelTemperature "C") [:< 20] 'temp:Low [20 :>] 'temp:High)
		 :probability
			 	(('biomass:Low|'temp:Low -> [0.7 0.9]) ('biomass:Low|'temp:High -> [0.1 0.3])
			 	 ('biomass:High|'temp:Low -> 0.017)    ('biomass:High|'temp:High -> 0.983))
		 :when (is? biome 'Aquatic))
			 	 
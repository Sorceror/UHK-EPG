package tvdata.enums;

	/**
	 * Typ zvuku ve vysilanem porade
	 * @author Pavel Janecka
	 */
	public enum SoundType {
		/** Mono zvuk */
		MONO("M"), 
		/** Stereo zvuk */
		STEREO("S"), 
		/** Prostorovy zvuk */
		DOLBI_DIGITAL("D"), 
		/** Porad obsahuje vice zvukovych kanaly */
		DUAL("B");
		
		private String strVal;
		
		/**
		 * Konstruktor enum hodnoty s jeji odpovidajici hodnotou v retezci
		 * @param strVal
		 */
		private SoundType(String strVal) {
			this.strVal = strVal;
		}
		
		/**
		 * Vraci enum hodnotu z odpovidajiciho retezce nebo {@link #STEREO} pokud zadna neodpovida
		 * @param str String retezec s hodnotou
		 * @return {@link SoundType}
		 */
		public static SoundType fromString(String str) {
			if (str.equalsIgnoreCase(MONO.strVal)) return MONO;
			if (str.equalsIgnoreCase(DOLBI_DIGITAL.strVal)) return DOLBI_DIGITAL;
			if (str.equalsIgnoreCase(DUAL.strVal)) return DUAL;
			return STEREO;
		}
	}

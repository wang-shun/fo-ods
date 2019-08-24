package com.foods.statediagram;

public final  class STATE {
	/*
	 * <value>NEW</value>
				<value>DONE</value>
				<value>VER</value>
				<value>INTERM</value>
				<value>MAT</value>
	 */
	public static final String NEW="NEW";
	public static final String DONE="DONE";
	public static final String VER="VER";
	public static final String PENDVER="PENDVER";
	public static final String INTERM="INTERM";
	public static final String PENDCANC="PENDCANC";
	public static final String CANC="CANC";
	public static final String MAT="MAT";
	
	public static class ACTION{
		/*
		 * <value>CREATE</value>
				<value>MATURE</value>
				<value>SAVE</value>
				<value>APPROVE</value>
				<value>REJECT</value>
				<value>BULK_FTERM</value>
				<value>BULK_WRITE</value>
				<value>BULK_APPROVE</value>
				<value>BULK_REJECT</value>
		 */
		
		public static final String CREATE="CREATE";
		public static final String MATURE="MATURE";
		public static final String SAVE="SAVE";
		public static final String FTERM="FTERM";
		public static final String PTERM="PTERM";
		public static final String APPROVE="APPROVE";
		public static final String REJECT="REJECT";
		public static final String CANCEL="CANCEL";
		public static final String BULK_TERM="BULK_TERM";
		public static final String BULK_WRITE="BULK_WRITE";
		public static final String BULK_APPROVE="BULK_APPROVE";
		public static final String BULK_REJECT="BULK_REJECT";
	}
	
}

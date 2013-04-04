/**
 * 
 */
package tvdata;

import java.net.URL;

import tvdata.enums.SoundType;

/**
 * Trida obsahuje dodatecne informace o poradu, ktere nemusi byt uvedene u kazdeho poradu (mohou byt null)
 * @author Pavel Janecka
 */
public class ProgramInfo {
	private String showName;
	private String originalName;
	private String episodeName;
	private String genre;
	private String pictureAspectRatio;
	private Integer episodesInSeason;
	private Integer episodeCount;
	private URL programURL;
	private URL streamPageURL;
	private SoundType soundType;
	private Boolean hiddenSubtitles;
	private Boolean commentsForDeafPeople;
	private Boolean liveShow;
	private Boolean premiere;
	private Boolean blackAndWhiteOnly;
	private Boolean notForChildren;
	private Boolean originalDubbing;

	/**
	 * @return the showName
	 */
	public String getShowName() {
		return showName;
	}
	/**
	 * @param showName the showName to set
	 */
	public void setShowName(String showName) {
		this.showName = showName;
	}
	/**
	 * @return the originalName
	 */
	public String getOriginalName() {
		return originalName;
	}
	/**
	 * @param originalName the originalName to set
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	/**
	 * @return the episodeName
	 */
	public String getEpisodeName() {
		return episodeName;
	}
	/**
	 * @param episodeName the episodeName to set
	 */
	public void setEpisodeName(String episodeName) {
		this.episodeName = episodeName;
	}
	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}
	/**
	 * @return the episodesInSeason
	 */
	public Integer getEpisodesInSeason() {
		return episodesInSeason;
	}
	/**
	 * @param episodesInSeason the episodesInSeason to set
	 */
	public void setEpisodesInSeason(Integer episodesInSeason) {
		this.episodesInSeason = episodesInSeason;
	}
	/**
	 * @return the episodeCount
	 */
	public Integer getEpisodeCount() {
		return episodeCount;
	}
	/**
	 * @param episodeCount the episodeCount to set
	 */
	public void setEpisodeCount(Integer episodeCount) {
		this.episodeCount = episodeCount;
	}
	/**
	 * @return the programURL
	 */
	public URL getProgramURL() {
		return programURL;
	}
	/**
	 * @param programURL the programURL to set
	 */
	public void setProgramURL(URL programURL) {
		this.programURL = programURL;
	}
	/**
	 * @return the streamPageURL
	 */
	public URL getStreamPageURL() {
		return streamPageURL;
	}
	/**
	 * @param streamPageURL the streamPageURL to set
	 */
	public void setStreamPageURL(URL streamPageURL) {
		this.streamPageURL = streamPageURL;
	}
	/**
	 * @return the soundType
	 */
	public SoundType getSoundType() {
		return soundType;
	}
	/**
	 * @param soundType the soundType to set
	 */
	public void setSoundType(SoundType soundType) {
		this.soundType = soundType;
	}
	/**
	 * @return the hiddenSubtitles
	 */
	public Boolean getHiddenSubtitles() {
		return hiddenSubtitles;
	}
	/**
	 * @param hiddenSubtitles the hiddenSubtitles to set
	 */
	public void setHiddenSubtitles(Boolean hiddenSubtitles) {
		this.hiddenSubtitles = hiddenSubtitles;
	}
	/**
	 * @return the commentsForDeafPeople
	 */
	public Boolean getCommentsForDeafPeople() {
		return commentsForDeafPeople;
	}
	/**
	 * @param commentsForDeafPeople the commentsForDeafPeople to set
	 */
	public void setCommentsForDeafPeople(Boolean commentsForDeafPeople) {
		this.commentsForDeafPeople = commentsForDeafPeople;
	}
	/**
	 * @return the liveShow
	 */
	public Boolean getLiveShow() {
		return liveShow;
	}
	/**
	 * @param liveShow the liveShow to set
	 */
	public void setLiveShow(Boolean liveShow) {
		this.liveShow = liveShow;
	}
	/**
	 * @return the premiere
	 */
	public Boolean getPremiere() {
		return premiere;
	}
	/**
	 * @param premiere the premiere to set
	 */
	public void setPremiere(Boolean premiere) {
		this.premiere = premiere;
	}
	/**
	 * @return the blackAndWhiteOnly
	 */
	public Boolean getBlackAndWhiteOnly() {
		return blackAndWhiteOnly;
	}
	/**
	 * @param blackAndWhiteOnly the blackAndWhiteOnly to set
	 */
	public void setBlackAndWhiteOnly(Boolean blackAndWhiteOnly) {
		this.blackAndWhiteOnly = blackAndWhiteOnly;
	}
	/**
	 * @return the notForChildren
	 */
	public Boolean getNotForChildren() {
		return notForChildren;
	}
	/**
	 * @param notForChildren the notForChildren to set
	 */
	public void setNotForChildren(Boolean notForChildren) {
		this.notForChildren = notForChildren;
	}
	/**
	 * @return the originalDabing
	 */
	public Boolean getOriginalDubbing() {
		return originalDubbing;
	}
	/**
	 * @param originalDubbing the originalDabing to set
	 */
	public void setOriginalDubbing(Boolean originalDubbing) {
		this.originalDubbing = originalDubbing;
	}
	/**
	 * @return the pictureAspectRatio
	 */
	public String getPictureAspectRatio() {
		return pictureAspectRatio;
	}
	/**
	 * @param pictureAspectRatio the pictureAspectRatio to set
	 */
	public void setPictureAspectRatio(String pictureAspectRatio) {
		this.pictureAspectRatio = pictureAspectRatio;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProgramInfo [showName=" + showName + ", originalName="
				+ originalName + ", episodeName=" + episodeName + ", genre="
				+ genre + ", pictureAspectRatio=" + pictureAspectRatio
				+ ", episodesInSeason=" + episodesInSeason + ", episodeCount="
				+ episodeCount + ", programURL=" + programURL
				+ ", streamPageURL=" + streamPageURL + ", soundType="
				+ soundType + ", hiddenSubtitles=" + hiddenSubtitles
				+ ", commentsForDeafPeople=" + commentsForDeafPeople
				+ ", liveShow=" + liveShow + ", premiere=" + premiere
				+ ", blackAndWhiteOnly=" + blackAndWhiteOnly
				+ ", notForChildren=" + notForChildren + ", originalDubbing="
				+ originalDubbing + "]";
	}
}

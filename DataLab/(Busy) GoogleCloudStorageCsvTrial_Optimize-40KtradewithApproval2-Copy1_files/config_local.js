const vid = document.body.getAttribute("data-version-id");
const GTM_ACCOUNT = "GTM-5CVQBG";
const LATEST_SEMVER = "1.2.20170818";
const OAUTH2_CLIENT_ID = "1065924671831-h94psic2ph6mfhpkc5dfeqiv2k6qgcub.apps.googleusercontent.com";
const version_tokens = vid.split('.');
if (version_tokens.length === 3) {
  const latest = version_tokens[2] < "20170523" ? "20170818" : LATEST_SEMVER;
  const LAST_SEMVER = "0.5.20160802";
  const PREV_SEMVER = "20170421";

  window.datalab.versions = {
    latest:   latest,
    last:     LAST_SEMVER,
    previous: PREV_SEMVER,
  };
}

window.datalab.gtmAccount = GTM_ACCOUNT;
window.datalab.oauth2ClientId = OAUTH2_CLIENT_ID;
window.dataLayer = window.dataLayer || [];
window.dataLayer.push({
	hostname:"datalab.cloud.google.com",
	pagePath:"/notebook/"
});

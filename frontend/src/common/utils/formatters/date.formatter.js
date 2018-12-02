import svLocale from "date-fns/locale/sv";
import formatDistance from "date-fns/formatDistance";
import _ from "lodash";

export function formatDateFromServer(time, locale) {
    const date = new Date(time * 1000);

    return _.capitalize(
        formatDistance(date, new Date(), {
            locale: locale === "sv" ? svLocale : null,
            includeSeconds: true,
            addSuffix: true
        })
    );
}

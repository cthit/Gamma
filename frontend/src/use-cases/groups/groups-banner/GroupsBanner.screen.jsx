import React from "react";
import { useDigitTranslations } from "@cthit/react-digit-components";
import translations from "./GroupsBanner.screen.translations.json";
import PutImage from "../../../common/elements/put-image";
import { uploadGroupBanner } from "../../../api/image/put.image.api";

const GroupsBanner = ({ match }) => {
    const [text] = useDigitTranslations(translations);

    return (
        <PutImage
            imageSrc={"/api/internal/groups/banner/" + match.params.id}
            title={text.GroupBannerTitle}
            uploadFile={file => uploadGroupBanner(file, match.params.id)}
        />
    );
};

export default GroupsBanner;

import some from "lodash/some";
import React from "react";

import { useDigitTranslations } from "@cthit/react-digit-components";

import { uploadGroupBanner } from "../../../api/image/put.image.api";

import PutImage from "../../../common/elements/put-image";
import useGammaIsAdmin from "../../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import useGammaUser from "../../../common/hooks/use-gamma-user/useGammaUser";
import InsufficientAccess from "../../../common/views/insufficient-access";

import translations from "./GroupsBanner.screen.translations.json";

const GroupsBanner = ({ match }) => {
    const [text] = useDigitTranslations(translations);
    const me = useGammaUser();
    const admin = useGammaIsAdmin();

    const isPartOfGroupOrAdmin =
        some(me.groups, ["group.id", match.params.id]) || admin;

    if (!isPartOfGroupOrAdmin) {
        return <InsufficientAccess />;
    }

    return (
        <PutImage
            imageSrc={"/api/images/group/banner/" + match.params.id}
            title={text.GroupBannerTitle}
            uploadFile={file => uploadGroupBanner(file, match.params.id)}
        />
    );
};

export default GroupsBanner;

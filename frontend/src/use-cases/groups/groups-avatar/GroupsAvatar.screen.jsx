import some from "lodash/some";
import React, { useContext } from "react";

import { useDigitTranslations } from "@cthit/react-digit-components";

import { uploadGroupAvatar } from "api/image/put.image.api";

import useGammaHasAuthority from "../../../common/hooks/use-gamma-has-authority/use-gamma-has-authority";
import useGammaIsAdmin from "../../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import useGammaUser from "../../../common/hooks/use-gamma-user/useGammaUser";
import InsufficientAccess from "../../../common/views/insufficient-access";
import PutImage from "common/elements/put-image";

import translations from "./GroupsAvatar.screen.translations.json";

const GroupsAvatar = ({ match }) => {
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
            imageSrc={"/api/images/group/avatar/" + match.params.id}
            title={text.GroupAvatarTitle}
            uploadFile={file => uploadGroupAvatar(file, match.params.id)}
        />
    );
};

export default GroupsAvatar;

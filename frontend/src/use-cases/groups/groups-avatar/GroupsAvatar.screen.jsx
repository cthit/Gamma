import React, { useContext } from "react";

import { useDigitTranslations } from "@cthit/react-digit-components";

import { uploadGroupAvatar } from "api/image/put.image.api";

import translations from "./GroupsAvatar.screen.translations.json";
import PutImage from "common/elements/put-image";

const GroupsAvatar = ({ match }) => {
    const [text] = useDigitTranslations(translations);

    return (
        <PutImage
            imageSrc={"/api/internal/groups/avatar/" + match.params.id}
            title={text.GroupAvatarTitle}
            uploadFile={file => uploadGroupAvatar(file, match.params.id)}
        />
    );
};

export default GroupsAvatar;

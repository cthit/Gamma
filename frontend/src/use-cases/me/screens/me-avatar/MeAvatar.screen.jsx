import React, { useContext } from "react";

import { useDigitTranslations } from "@cthit/react-digit-components";

import { uploadUserAvatar } from "api/image/put.image.api";

import PutImage from "../../../../common/elements/put-image";
import GammaUserContext from "common/context/GammaUser.context";

import translations from "./MeAvatar.screen.translations";

const MeAvatar = () => {
    const [text] = useDigitTranslations(translations);
    const [user] = useContext(GammaUserContext);

    return (
        <PutImage
            imageSrc={"/api/images/user/avatar/" + user.id}
            title={text.MeAvatarTitle}
            uploadFile={uploadUserAvatar}
        />
    );
};

export default MeAvatar;

import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
    ACCEPTANCE_YEAR,
    CID,
    EMAIL,
    FIRST_NAME,
    LANGUAGE,
    LAST_NAME,
    NICK,
    PASSWORD,
    WEBSITES
} from "../../api/users/props.users.api";
import {
    useDigitTranslations,
    DigitCRUD,
    DigitTextField,
    DigitSelect
} from "@cthit/react-digit-components";
import translations from "./Me.translations.json";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { editMe } from "../../api/me/put.me.api";
import { getWebsites } from "../../api/websites/get.websites.api";
import { userUpdateMe } from "../../app/elements/user-information/UserInformation.element.action-creator";
import {
    generateUserCustomDetailsRenders,
    generateUserEditComponentData,
    generateUserKeysTexts,
    generateUserValidationSchema
} from "../../common/utils/generators/user-form.generator";

const Me = () => {
    const [text] = useDigitTranslations(translations);
    const [websites, setWebsites] = useState(null);
    const dispatch = useDispatch();
    const me = useSelector(state => state.user);

    useEffect(() => {
        getWebsites().then(response => {
            setWebsites(response.data);
            dispatch(gammaLoadingFinished());
        });
    }, []);

    if (!me.loaded || websites == null) {
        return null;
    }

    return (
        <DigitCRUD
            backFromReadOneLink={"/"}
            name={"me"}
            path={"/me"}
            staticId={null}
            readOnePath={""}
            updatePath={"/edit"}
            readOneRequest={() => dispatch(userUpdateMe())}
            keysOrder={[
                FIRST_NAME,
                LAST_NAME,
                NICK,
                EMAIL,
                ACCEPTANCE_YEAR,
                LANGUAGE,
                WEBSITES
            ]}
            updateRequest={(id, newData) => editMe(newData)}
            customDetailsRenders={generateUserCustomDetailsRenders()}
            keysText={generateUserKeysTexts(text)}
            formValidationSchema={generateUserValidationSchema(text)}
            formComponentData={generateUserEditComponentData(text, websites)}
        />
    );
};

export default Me;

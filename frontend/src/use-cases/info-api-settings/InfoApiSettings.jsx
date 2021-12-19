import React, { useEffect, useState } from "react";
import Translations from "./InfoApiSettings.translations.json";
import {
    DigitAutocompleteSelectMultiple,
    DigitEditDataCard,
    DigitLoading,
    useDigitToast,
    useDigitTranslations
} from "@cthit/react-digit-components";
import { getInfoApiSuperGroupTypes } from "../../api/info-api-settings/get.info-api-settings";
import { getSuperGroupTypes } from "../../api/super-group-types/get.super-group-types.api";
import { setInfoApiSuperGroupTypes } from "../../api/info-api-settings/post.info-api-settings";

const InfoApiSettings = ({}) => {
    const [types, setTypes] = useState(null);
    const [availableTypes, setAvailableTypes] = useState();
    const [text] = useDigitTranslations(Translations);
    const [queueToast] = useDigitToast();

    useEffect(() => {
        getInfoApiSuperGroupTypes().then(response => {
            setTypes(response.data);
        });
        getSuperGroupTypes().then(response =>
            setAvailableTypes(
                response.map(({ type }) => ({ text: type, value: type }))
            )
        );
    }, []);

    const onSubmit = values => {
        setInfoApiSuperGroupTypes(values).then(() =>
            queueToast({ text: "Successfully set the super group types" })
        );
    };

    if (types == null || availableTypes == null) {
        return <DigitLoading loading alignSelf={"center"} margin={"auto"} />;
    }

    console.log(availableTypes);

    return (
        <DigitEditDataCard
            initialValues={{ superGroupTypes: types }}
            onSubmit={onSubmit}
            keysComponentData={{
                superGroupTypes: {
                    component: DigitAutocompleteSelectMultiple,
                    componentProps: {
                        filled: true,
                        options: availableTypes
                    }
                }
            }}
            keysOrder={["superGroupTypes"]}
            cardProps={{
                size: { maxWidth: "350px", width: "280px" },
                margin: "auto"
            }}
            submitText={text.Save}
            titleText={text.InfoApiSettingsTitle}
            subtitleText={text.InfoApiSettingsDescription}
        />
    );
};

export default InfoApiSettings;

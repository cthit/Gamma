import React from "react";
import { useHistory } from "react-router-dom";

import {
    useDigitTranslations,
    DigitEditDataCard,
    DigitLayout,
    useDigitToast
} from "@cthit/react-digit-components";

import { activateCid } from "api/whitelist/post.whitelist.api";

import ChangeLanguageLocally from "common/views/change-language-locally";

import {
    initialValues,
    keysComponentData,
    keysOrder,
    validationSchema
} from "./InputCid.view.options";
import translations from "./InputCid.view.translations";

const InputCid = () => {
    const [queueToast] = useDigitToast();
    const [text] = useDigitTranslations(translations);
    const history = useHistory();

    return (
        <DigitLayout.Center>
            <ChangeLanguageLocally />
            <DigitEditDataCard
                validationSchema={validationSchema(text)}
                initialValues={initialValues()}
                keysComponentData={keysComponentData(text)}
                keysOrder={keysOrder()}
                centerFields
                onSubmit={(values, actions) => {
                    activateCid(values)
                        .then(() => {
                            actions.resetForm();
                            actions.setSubmitting(false);
                            history.push("/create-account/email-sent");
                        })
                        .catch(() => {
                            queueToast({
                                text: text.SomethingWentWrong,
                                duration: 3000
                            });
                        });
                }}
                size={{ width: "300px", height: "300px" }}
                titleText={text.EnterYourCid}
                subtitleText={text.EnterYourCidDescription}
                submitText={text.SendCid}
                extraButton={{
                    text: text.AlreadyHaveCode
                }}
                extraButtonTo={"/create-account/email-sent"}
            />
        </DigitLayout.Center>
    );
};

export default InputCid;

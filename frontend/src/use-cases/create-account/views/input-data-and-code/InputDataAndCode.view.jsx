import axios from "axios";
import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";

import {
    DigitButton,
    DigitDesign,
    useDigitTranslations,
    useDigitToast,
    DigitEditDataCard,
    useDigitCustomDialog,
    DigitMarkdown
} from "@cthit/react-digit-components";

import { createAccount } from "api/create-account/post.createAccount.api";

import statusCode from "common/utils/formatters/statusCode.formatter";
import statusMessage from "common/utils/formatters/statusMessage.formatter";

import {
    initialValues,
    keysComponentData,
    keysOrder,
    validationSchema
} from "./InputDataAndCode.view.options";
import translations from "./InputDataAndCode.view.translations.json";

const InputDataAndCode = () => {
    const [text, activeLanguage] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();
    const [showDialog] = useDigitCustomDialog();
    const history = useHistory();
    const [userAgreement, setUserAgreement] = useState();

    useEffect(() => {
        axios.get("/useragreement-" + activeLanguage + ".md").then(response => {
            setUserAgreement(response.data);
        });
    }, [activeLanguage]);

    return (
        <>
            <DigitDesign.Card alignSelf={"center"}>
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle text={text.UserAgreement} />
                </DigitDesign.CardHeader>
                <DigitDesign.CardBody>
                    <DigitButton
                        text={text.ShowUserAgreement}
                        primary
                        raised
                        onClick={() => {
                            showDialog({
                                renderMain: () => (
                                    <DigitMarkdown
                                        size={{
                                            minWidth: "320px",
                                            width: "100%",
                                            maxWidth: "600px"
                                        }}
                                        markdownSource={userAgreement}
                                    />
                                ),
                                renderButtons: confirm => (
                                    <DigitButton
                                        primary
                                        raised
                                        text={text.Close}
                                        onClick={confirm}
                                    />
                                )
                            });
                        }}
                    />
                </DigitDesign.CardBody>
            </DigitDesign.Card>
            <DigitEditDataCard
                alignSelf={"center"}
                initialValues={initialValues()}
                validationSchema={validationSchema(text)}
                keysOrder={keysOrder()}
                keysComponentData={keysComponentData(text)}
                centerFields
                titleText={text.CompleteCreation}
                subtitleText={text.CompleteCreationDescription}
                submitText={text.CreateAccount}
                extraButton={{
                    outlined: true,
                    text: text.Back,
                    onClick: () => history.goBack()
                }}
                onSubmit={(values, actions) => {
                    const cid = values.cid;
                    const user = {
                        whitelist: {
                            cid: cid
                        },
                        ...values
                    };
                    createAccount(user)
                        .then(() => {
                            actions.resetForm();
                            history.push("/create-account/finished");
                        })
                        .catch(error => {
                            const code = statusCode(error);
                            const message = statusMessage(error);
                            var errorMessage;
                            switch (code) {
                                case 422:
                                    switch (message) {
                                        case "CODE_OR_CID_IS_WRONG":
                                            errorMessage =
                                                text.CODE_OR_CID_IS_WRONG;
                                            break;
                                        case "TOO_SHORT_PASSWORD":
                                            errorMessage =
                                                text.TOO_SHORT_PASSWORD;
                                            break;
                                        default:
                                            errorMessage =
                                                text.SomethingWentWrong;
                                    }
                                    break;
                                default:
                                    errorMessage = text.SomethingWentWrong;
                            }
                            queueToast({
                                text: errorMessage,
                                duration: 5000
                            });
                        });
                }}
                size={{ minWidth: "300px", maxWidth: "600px" }}
            />
        </>
    );
};

export default InputDataAndCode;

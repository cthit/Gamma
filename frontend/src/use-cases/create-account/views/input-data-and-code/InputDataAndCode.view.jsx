import {
    DigitLayout,
    useDigitTranslations,
    useDigitToast,
    DigitEditDataCard
} from "@cthit/react-digit-components";
import React from "react";
import statusCode from "../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../common/utils/formatters/statusMessage.formatter";
import translations from "./InputDataAndCode.view.translations.json";
import { useHistory } from "react-router-dom";
import { createAccount } from "../../../../api/create-account/post.createAccount.api";
import {
    initialValues,
    keysComponentData,
    keysOrder,
    validationSchema
} from "./InputDataAndCode.view.options";

const InputDataAndCode = () => {
    const [text] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();
    const history = useHistory();

    return (
        <DigitLayout.Center>
            <DigitEditDataCard
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
        </DigitLayout.Center>
    );
};

export default InputDataAndCode;

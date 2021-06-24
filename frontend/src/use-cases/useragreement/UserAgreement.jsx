import React from "react";
import {
    useDigitTranslations,
    DigitDesign,
    DigitText,
    DigitButton,
    useDigitCustomDialog,
    DigitEditData,
    DigitTextField
} from "@cthit/react-digit-components";
import translations from "./UserAgreement.translations.json";
import { deleteMe } from "../../api/me/delete.me.api";
import { getBackendUrl } from "../../common/utils/configs/envVariablesLoader";
import * as yup from "yup";
import { resetUserAgreement } from "../../api/user-agreement/post.user-agreement";

const UserAgreement = () => {
    const [text] = useDigitTranslations(translations);
    const [openCustomDialog] = useDigitCustomDialog();

    return (
        <DigitDesign.Card size={{ width: "280px" }} margin={"auto"}>
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={text.UATitle} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardBody>
                <DigitText.Text text={text.UADescription} />
            </DigitDesign.CardBody>
            <DigitDesign.CardButtons>
                <DigitButton
                    flex={"1"}
                    text={text.UAReset}
                    outlined
                    onClick={() =>
                        openCustomDialog({
                            title: text.AreYouReallySure,
                            renderMain: () => (
                                <>
                                    <DigitText.Text
                                        text={text.EnterYourPassword}
                                    />
                                    <DigitEditData
                                        centerFields
                                        keysComponentData={{
                                            password: {
                                                component: DigitTextField,
                                                componentProps: {
                                                    upperLabel: text.Password,
                                                    password: true,
                                                    outlined: true,
                                                    autoComplete: false
                                                }
                                            }
                                        }}
                                        onSubmit={passwordData =>
                                            resetUserAgreement(passwordData)
                                        }
                                        keysOrder={["password"]}
                                        submitText={text.UAReset}
                                        validationSchema={yup.object().shape({
                                            password: yup
                                                .string()
                                                .min(8)
                                                .required(
                                                    text.YouMustEnterPassword
                                                )
                                        })}
                                        initialValues={{ password: "" }}
                                        formName={"reset-user-agreement"}
                                    />
                                </>
                            ),
                            renderButtons: (confirm, cancel) => (
                                <div
                                    style={{
                                        width: "100%",
                                        display: "flex",
                                        flexDirection: "row",
                                        justifyContent: "space-between"
                                    }}
                                >
                                    <DigitButton
                                        onClick={cancel}
                                        outlined
                                        text={text.Cancel}
                                    />
                                    <DigitButton
                                        onClick={confirm}
                                        outlined
                                        text={text.UAReset}
                                        submit
                                        form={"reset-user-agreement"}
                                    />
                                </div>
                            )
                        })
                    }
                />
            </DigitDesign.CardButtons>
        </DigitDesign.Card>
    );
};

export default UserAgreement;

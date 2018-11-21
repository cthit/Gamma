import {
    DigitEditData,
    DigitLayout,
    DigitTextField,
    DigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import translations from "./EditActivationCodeDetails.screen.translations.json";

const EditActivationCodeDetails = ({
    activationCode,
    activationCodesChange
}) => (
    <IfElseRendering
        test={activationCode != null}
        ifRender={text => (
            <DigitTranslations
                translations={translations}
                uniquePath="ActivationCodes.Screen.EditActivationCodeDetails"
                render={text => (
                    <DigitLayout.Center>
                        <DigitEditData
                            initialValues={activationCode}
                            onSubmit={(values, actions) => {
                                console.log(values);
                            }}
                            validationSchema={yup.object().shape({
                                code: yup.string().required(text.FieldRequired)
                            })}
                            titleText={text.EditActivationCode}
                            submitText={text.SaveActivationCode}
                            keysOrder={["code"]}
                            keysComponentData={{
                                code: {
                                    component: DigitTextField,
                                    componentProps: {
                                        upperLabel: text.Code
                                    }
                                }
                            }}
                        />
                    </DigitLayout.Center>
                )}
            />
        )}
    />
);

export default EditActivationCodeDetails;

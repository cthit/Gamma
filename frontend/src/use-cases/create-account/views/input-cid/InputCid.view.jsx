import { DigitButton, DigitTranslations } from "@cthit/react-digit-components";
import PropTypes from "prop-types";
import React from "react";
import * as yup from "yup";
import {
    GammaCard,
    GammaCardBody,
    GammaCardButtons,
    GammaCardSubTitle,
    GammaCardTitle
} from "../../../../common-ui/design";
import { Center, MarginTop } from "../../../../common-ui/layout";
import { CIDInput } from "./InputCid.view.styles";
import translations from "./InputCid.view.translations";

const InputCid = ({ sendCid, redirectTo, toastOpen }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="CreateAccount"
        render={text => (
            <MarginTop>
                <Center>
                    <DigitForm
                        validationSchema={yup.object().shape({
                            cid: yup.string().required(text.FieldRequired)
                        })}
                        initialValues={{ cid: "" }}
                        onSubmit={(values, actions) => {
                            sendCid(values)
                                .then(response => {
                                    actions.resetForm();
                                    redirectTo("/create-account/email-sent");
                                })
                                .catch(error => {
                                    toastOpen({
                                        text: text.SomethingWentWrong,
                                        duration: 10000
                                    });
                                });
                        }}
                        render={({ errors, touched }) => (
                            <GammaCard
                                absWidth="300px"
                                absHeight="300px"
                                hasSubTitle
                            >
                                <GammaCardTitle text={text.EnterYourCid} />
                                <GammaCardSubTitle
                                    text={text.EnterYourCidDescription}
                                />
                                <GammaCardBody>
                                    <Center>
                                        <DigitFormField
                                            name="cid"
                                            component={CIDInput}
                                            componentProps={{
                                                upperLabel: text.Cid
                                            }}
                                        />
                                    </Center>
                                </GammaCardBody>
                                <GammaCardButtons reverseDirection>
                                    <DigitButton
                                        text={text.SendCid}
                                        primary
                                        raised
                                        submit
                                    />
                                </GammaCardButtons>
                            </GammaCard>
                        )}
                    />
                </Center>
            </MarginTop>
        )}
    />
);

InputCid.propTypes = {
    sendCid: PropTypes.func.isRequired
};

export default InputCid;

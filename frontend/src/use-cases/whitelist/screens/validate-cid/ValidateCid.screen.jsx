import { DigitLayout, DigitTranslations } from "@cthit/react-digit-components";
import React from "react";
import WhitelistItemForm from "../common-views/whitelist-item-form/WhitelistItemForm.view";
import translations from "./ValidateCid.screen.translations.json";

const ValidateCid = ({ whitelistValidate, toastOpen }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Whitelist.Screen.ValdiateCid"
        render={text => (
            <DigitLayout.Center>
                <WhitelistItemForm
                    initialValues={{ cid: "" }}
                    onSubmit={(values, actions) => {
                        whitelistValidate(values.cid)
                            .then(response => {
                                if (response) {
                                    toastOpen({
                                        text: text.CidIsValid
                                    });
                                } else {
                                    toastOpen({
                                        text: text.CidIsNotValid
                                    });
                                }
                            })
                            .catch(error => {
                                toastOpen({
                                    text: text.SomethingWentWrong,
                                    duration: 10000
                                });
                            });
                    }}
                    titleText={text.ValidateCid}
                    cidInputText={text.CidToValidate}
                    fieldRequiredText={text.FieldRequired}
                    submitText={text.Validate}
                />
            </DigitLayout.Center>
        )}
    />
);

export default ValidateCid;

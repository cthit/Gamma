import {
    DigitIfElseRendering,
    DigitLayout,
    DigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import WhitelistItemForm from "../common-views/whitelist-item-form";
import translations from "./EditWhitelistItemDetails.screen.translations.json";

const EditWhitelistItemDetails = ({
    whitelistChange,
    whitelistItem,
    match,
    toastOpen
}) => (
    <DigitIfElseRendering
        test={whitelistItem != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Whitelist.Screen.EditWhitelistItemDetails"
                render={text => (
                    <DigitLayout.Fill>
                        <DigitLayout.Center>
                            <WhitelistItemForm
                                onSubmit={(values, actions) => {
                                    whitelistChange(values, match.params.id)
                                        .then(response => {
                                            toastOpen({
                                                text: text.SuccessfullyEdit
                                            });
                                            actions.resetForm();
                                        })
                                        .catch(error => {
                                            toastOpen({
                                                text: text.SomethingWentWrong,
                                                duration: 6000
                                            });
                                        });
                                }}
                                initialValues={{ cid: whitelistItem.cid }}
                                titleText={text.EditCid}
                                cidInputText={text.Cid}
                                fieldRequiredText={text.FieldRequired}
                                submitText={text.SaveCid}
                            />
                        </DigitLayout.Center>
                    </DigitLayout.Fill>
                )}
            />
        )}
    />
);

export default EditWhitelistItemDetails;

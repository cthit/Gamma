import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData,
    DigitDesign,
    DigitLayout,
    DigitIfElseRendering
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./ShowActivationCodeDetails.screen.translations.json";
import { formatDateFromServer } from "../../../../common/utils/formatters/date.formatter";

const ShowActivationCodeDetails = ({
    activationCode,
    deleteActivationCode,
    dialogOpen,
    redirectTo,
    toastOpen
}) => (
    <DigitIfElseRendering
        test={activationCode != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="ActivationCode.Screen.ShowActivationCodeDetails"
                render={(text, activeLanguage) => (
                    <DigitLayout.Fill>
                        <DigitLayout.Center>
                            <DigitDesign.Card minWidth="300px" maxWidth="600px">
                                <DigitDesign.CardBody>
                                    <DigitDisplayData
                                        data={{
                                            ...activationCode,
                                            createdAt: formatDateFromServer(
                                                activationCode.createdAt,
                                                activeLanguage
                                            )
                                        }}
                                        keysText={{
                                            id: text.Id,
                                            cid: text.Cid,
                                            code: text.Code,
                                            createdAt: text.CreatedAt
                                        }}
                                        keysOrder={[
                                            "id",
                                            "cid",
                                            "code",
                                            "createdAt"
                                        ]}
                                    />
                                </DigitDesign.CardBody>
                                <DigitDesign.CardButtons reverseDirection>
                                    <DigitButton
                                        text={text.DeleteActivationCode}
                                        onClick={() => {
                                            dialogOpen({
                                                title:
                                                    text.WouldYouLikeToDelete +
                                                    " " +
                                                    activationCode.cid,
                                                confirmButtonText:
                                                    text.DeleteActivationCode,
                                                cancelButtonText: text.Cancel,
                                                onConfirm: () => {
                                                    deleteActivationCode(
                                                        activationCode.id
                                                    )
                                                        .then(response => {
                                                            toastOpen({
                                                                text:
                                                                    text.YouHaveDeleted +
                                                                    " " +
                                                                    activationCode.cid
                                                            });
                                                            redirectTo(
                                                                "/activation-codes"
                                                            );
                                                        })
                                                        .catch(error => {
                                                            toastOpen({
                                                                text:
                                                                    text.SomethingWentWrong
                                                            });
                                                        });
                                                }
                                            });
                                        }}
                                    />
                                </DigitDesign.CardButtons>
                            </DigitDesign.Card>
                        </DigitLayout.Center>
                    </DigitLayout.Fill>
                )}
            />
        )}
    />
);

export default ShowActivationCodeDetails;

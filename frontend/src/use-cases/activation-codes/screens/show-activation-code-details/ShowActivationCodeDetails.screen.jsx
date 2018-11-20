import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData
} from "@cthit/react-digit-components";
import React from "react";
import {
    GammaCard,
    GammaCardBody,
    GammaCardButtons,
    GammaLink
} from "../../../../common-ui/design";
import { Center, Fill } from "../../../../common-ui/layout";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import translations from "./ShowActivationCodeDetails.screen.translations.json";

const ShowActivationCodeDetails = ({
    activationCode,
    activationCodesDelete,
    gammaDialogOpen,
    redirectTo,
    toastOpen
}) => (
    <IfElseRendering
        test={activationCode != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="ActivationCode.Screen.ShowActivationCodeDetails"
                render={text => (
                    <Fill>
                        <Center>
                            <GammaCard minWidth="300px" maxWidth="600px">
                                <GammaCardBody>
                                    <DigitDisplayData
                                        data={activationCode}
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
                                </GammaCardBody>
                                <GammaCardButtons reverseDirection>
                                    <GammaLink
                                        to={
                                            "/activation-codes/" +
                                            activationCode.id +
                                            "/edit"
                                        }
                                    >
                                        <DigitButton
                                            text={text.EditActivationCode}
                                            primary
                                            raised
                                        />
                                    </GammaLink>
                                    <DigitButton
                                        text={text.DeleteActivationCode}
                                        onClick={() => {
                                            gammaDialogOpen({
                                                title:
                                                    text.WouldYouLikeToDelete +
                                                    " " +
                                                    activationCode.cid,
                                                confirmButtonText:
                                                    text.DeleteActivationCode,
                                                cancelButtonText: text.Cancel,
                                                onConfirm: () => {
                                                    activationCodesDelete(
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
                                </GammaCardButtons>
                            </GammaCard>
                        </Center>
                    </Fill>
                )}
            />
        )}
    />
);

export default ShowActivationCodeDetails;

import React from "react";
import { Fill, Center, Spacing } from "../../../../common-ui/layout";

import {
    GammaCard,
    GammaCardTitle,
    GammaCardBody,
    GammaLink,
    GammaCardButtons
} from "../../../../common-ui/design";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Text } from "../../../../common-ui/text";
import GammaButton from "../../../../common/elements/gamma-button";
import GammaDisplayData from "../../../../common/elements/gamma-display-data/GammaDisplayData.element";
import { DigitTranslations } from "@cthit/react-digit-components";
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
                                    <GammaDisplayData
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
                                        <GammaButton
                                            text={text.EditActivationCode}
                                            primary
                                            raised
                                        />
                                    </GammaLink>
                                    <GammaButton
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

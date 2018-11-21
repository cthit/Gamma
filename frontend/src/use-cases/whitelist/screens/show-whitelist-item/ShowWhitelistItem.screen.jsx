import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData,
    DigitDesign
} from "@cthit/react-digit-components";
import React from "react";
import { Center, Fill, Spacing } from "../../../../common-ui/layout";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import translations from "./ShowWhitelistItem.screen.translations.json";

const ShowWhitelistItem = ({
    whitelistItem,
    whitelistDelete,
    redirectTo,
    toastOpen,
    gammaDialogOpen
}) => (
    <IfElseRendering
        test={whitelistItem != null}
        ifRender={() => (
            <DigitTranslations
                translations={translations}
                uniquePath="Whitelist.Screen.ShowWhitelistItem"
                render={text => (
                    <Fill>
                        <Center>
                            <DigitDesign.Card minWidth="300px" maxWidth="600px">
                                <DigitDesign.CardBody>
                                    <DigitDisplayData
                                        data={whitelistItem}
                                        keysOrder={["id", "cid"]}
                                        keysText={{
                                            id: text.Id,
                                            cid: text.Cid
                                        }}
                                    />
                                </DigitDesign.CardBody>
                                <DigitDesign.CardButtons reversedDirection>
                                    <DigitButton
                                        text={text.DeleteWhitelistItem}
                                        onClick={() => {
                                            gammaDialogOpen({
                                                title:
                                                    text.WouldYouLikeToDelete +
                                                    " " +
                                                    whitelistItem.cid +
                                                    "?",
                                                confirmButtonText:
                                                    text.DeleteWhitelistItem,
                                                cancelButtonText: text.Cancel,
                                                onConfirm: () => {
                                                    whitelistDelete(
                                                        whitelistItem.id
                                                    )
                                                        .then(response => {
                                                            toastOpen({
                                                                text:
                                                                    text.DeleteSuccessfully +
                                                                    " " +
                                                                    whitelistItem.cid
                                                            });
                                                            redirectTo(
                                                                "/whitelist"
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
                                    <Spacing />
                                    <DigitDesign.Link
                                        to={
                                            "/whitelist/" +
                                            whitelistItem.id +
                                            "/edit"
                                        }
                                    >
                                        <DigitButton
                                            text={text.EditWhitelistItem}
                                            primary
                                            raised
                                        />
                                    </DigitDesign.Link>
                                </DigitDesign.CardButtons>
                            </DigitDesign.Card>
                        </Center>
                    </Fill>
                )}
            />
        )}
    />
);

export default ShowWhitelistItem;

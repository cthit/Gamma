import {
    DigitTranslations,
    DigitTable,
    DigitLayout
} from "@cthit/react-digit-components";
import React, { Component } from "react";
import translations from "./ShowAllActivationCodes.screen.translations.json";
import { formatDateFromServer } from "../../../../common/utils/formatters/date.formatter";
import * as PropTypes from "prop-types";

class ShowAllActivationCodes extends Component {
    componentDidMount() {
        const { getActivationCodes, gammaLoadingFinished } = this.props;

        getActivationCodes().then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        const { activationCodes } = this.props;
        return (
            <DigitLayout.Fill>
                <DigitTranslations
                    translations={translations}
                    uniquePath="ActivationCodes.Screen.ShowAllActivationCodes"
                    render={(text, activeLanguage) => (
                        <DigitTable
                            titleText={text.ActivationCodes}
                            searchText={text.SearchForActivationCodes}
                            idProp="id"
                            startOrderBy="cid"
                            columnsOrder={["id", "cid", "code", "createdAt"]}
                            headerTexts={{
                                id: text.Id,
                                cid: text.Cid,
                                code: text.Code,
                                createdAt: text.CreatedAt,
                                __link: text.Details
                            }}
                            data={activationCodes.map(activationCode => {
                                return {
                                    ...activationCode,
                                    __link:
                                        "/activation-codes/" +
                                        activationCode.id,
                                    createdAt: formatDateFromServer(
                                        activationCode.createdAt,
                                        activeLanguage
                                    )
                                };
                            })}
                            emptyTableText={text.NoActivationCodes}
                        />
                    )}
                />
            </DigitLayout.Fill>
        );
    }
}

export default ShowAllActivationCodes;

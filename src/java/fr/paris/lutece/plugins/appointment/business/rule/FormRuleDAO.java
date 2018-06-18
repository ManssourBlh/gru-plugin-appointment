/*
 * Copyright (c) 2002-2018, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.business.rule;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form Rule objects
 * 
 * @author Laurent Payen
 *
 */
public final class FormRuleDAO extends UtilDAO implements IFormRuleDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_form_rule) FROM appointment_form_rule";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_form_rule (id_form_rule, is_captcha_enabled, is_mandatory_email_enabled, is_active_authentication, nb_days_before_new_appointment, min_time_before_appointment, nb_max_appointments_per_user, nb_days_for_max_appointments_per_user, id_form) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_form_rule SET is_captcha_enabled = ?, is_mandatory_email_enabled = ?, is_active_authentication = ?, nb_days_before_new_appointment = ?, min_time_before_appointment = ?, nb_max_appointments_per_user = ?, nb_days_for_max_appointments_per_user = ?, id_form = ? WHERE id_form_rule = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_form_rule WHERE id_form_rule = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_form_rule, is_captcha_enabled, is_mandatory_email_enabled, is_active_authentication, nb_days_before_new_appointment, min_time_before_appointment, nb_max_appointments_per_user, nb_days_for_max_appointments_per_user, id_form FROM appointment_form_rule";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form_rule = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";

    @Override
    public synchronized void insert( FormRule formRule, Plugin plugin )
    {
        formRule.setIdFormRule( getNewPrimaryKey( SQL_QUERY_NEW_PK, plugin ) );
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, formRule, plugin, true );
        executeUpdate( daoUtil );
    }

    @Override
    public void update( FormRule formRule, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, formRule, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdFormRule, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdFormRule );
        executeUpdate( daoUtil );
    }

    @Override
    public FormRule select( int nIdFormRule, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        FormRule formRule = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdFormRule );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                formRule = buildFormRule( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return formRule;
    }

    @Override
    public FormRule findByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        FormRule formRule = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                formRule = buildFormRule( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return formRule;
    }

    /**
     * Build a Form rule business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new formRule with all its attributes assigned
     */
    private FormRule buildFormRule( DAOUtil daoUtil )
    {
        int nIndex = 1;
        FormRule formRule = new FormRule( );
        formRule.setIdFormRule( daoUtil.getInt( nIndex++ ) );
        formRule.setIsCaptchaEnabled( daoUtil.getBoolean( nIndex++ ) );
        formRule.setIsMandatoryEmailEnabled( daoUtil.getBoolean( nIndex++ ) );
        formRule.setIsActiveAuthentication( daoUtil.getBoolean( nIndex++ ) );
        formRule.setNbDaysBeforeNewAppointment( daoUtil.getInt( nIndex++ ) );
        formRule.setMinTimeBeforeAppointment( daoUtil.getInt( nIndex++ ) );
        formRule.setNbMaxAppointmentsPerUser( daoUtil.getInt( nIndex++ ) );
        formRule.setNbDaysForMaxAppointmentsPerUser( daoUtil.getInt( nIndex++ ) );
        formRule.setIdForm( daoUtil.getInt( nIndex ) );
        return formRule;
    }

    /**
     * Build a daoUtil object with the FormRule business object
     * 
     * @param query
     *            the query
     * @param formRule
     *            the FormRule
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, FormRule formRule, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( query, plugin );
        if ( isInsert )
        {
            daoUtil.setInt( nIndex++, formRule.getIdFormRule( ) );
        }
        daoUtil.setBoolean( nIndex++, formRule.getIsCaptchaEnabled( ) );
        daoUtil.setBoolean( nIndex++, formRule.getIsMandatoryEmailEnabled( ) );
        daoUtil.setBoolean( nIndex++, formRule.getIsActiveAuthentication( ) );
        daoUtil.setInt( nIndex++, formRule.getNbDaysBeforeNewAppointment( ) );
        daoUtil.setInt( nIndex++, formRule.getMinTimeBeforeAppointment( ) );
        daoUtil.setInt( nIndex++, formRule.getNbMaxAppointmentsPerUser( ) );
        daoUtil.setInt( nIndex++, formRule.getNbDaysForMaxAppointmentsPerUser( ) );
        daoUtil.setInt( nIndex++, formRule.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, formRule.getIdFormRule( ) );
        }
        return daoUtil;
    }

    /**
     * Execute a safe update (Free the connection in case of error when execute the query)
     * 
     * @param daoUtil
     *            the daoUtil
     */
    private void executeUpdate( DAOUtil daoUtil )
    {
        try
        {
            daoUtil.executeUpdate( );
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
    }

}

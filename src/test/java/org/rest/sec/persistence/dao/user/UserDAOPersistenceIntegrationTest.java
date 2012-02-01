package org.rest.sec.persistence.dao.user;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.rest.persistence.AbstractPersistenceDaoIntegrationTest;
import org.rest.sec.model.Role;
import org.rest.sec.model.User;
import org.rest.sec.persistence.dao.IRoleJpaDAO;
import org.rest.sec.persistence.dao.IUserJpaDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

@TransactionConfiguration( defaultRollback = true )
@Transactional
public class UserDAOPersistenceIntegrationTest extends AbstractPersistenceDaoIntegrationTest< User >{
	
	@Autowired
	private IUserJpaDAO dao;
	
	@Autowired
	private IRoleJpaDAO associationDao;
	
	// involving other entities
	
	@Test
	public void whenUserIsCreated_thenRolesOfUserAreLoaded(){
		final User persistedUser = this.persistNewEntity();
		
		assertThat( persistedUser.getRoles(), notNullValue() );
	}
	@Test
	public void whenUserIsCreated_thenCorectPrivilegesAreLoaded(){
		final String nameOfRole = "testRole";
		
		final User userWitoutRoles = this.persistNewEntity();
		final Role savedAssociation = associationDao.save( new Role( nameOfRole ) );
		userWitoutRoles.getRoles().add( savedAssociation );
		final User userWithPrivilege = getDAO().save( userWitoutRoles );
		
		assertThat( userWithPrivilege.getRoles(), contains( new Role( nameOfRole ) ) );
	}
	
	// save
	
	@Test
	public void whenSaveIsPerformed_thenNoException(){
		dao.save( new User( randomAlphabetic( 8 ), randomAlphabetic( 8 ) ) );
	}
	
	// find by
	
	@Test
	public void givenEntityDoesNotExist_whenFindingEntityByName_thenEntityNotFound(){
		// Given
		final String name = randomAlphabetic( 8 );
		
		// When
		final User entityByName = dao.findByName( name );
		
		// Then
		assertNull( entityByName );
	}
	
	// template method
	
	@Override
	protected final IUserJpaDAO getDAO(){
		return dao;
	}
	
	@Override
	protected final User createNewEntity(){
		final User user = new User( randomAlphabetic( 8 ), randomAlphabetic( 8 ) );
		user.setRoles( Sets.<Role> newHashSet() );
		return user;
	}
	
}

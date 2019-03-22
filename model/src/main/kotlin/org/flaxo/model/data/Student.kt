package org.flaxo.model.data

import org.flaxo.common.data.Identifiable
import java.util.Objects
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Student entity.
 */
@Entity(name = "student")
@Table(name = "student")
data class Student(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        // TODO 23.03.19: Rename to name.
        val nickname: String = "",

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        val course: Course = Course(),

        @OneToMany(mappedBy = "student", orphanRemoval = true)
        val solutions: Set<Solution> = mutableSetOf()

) : Identifiable, Viewable<String> {

    override fun view(): String = nickname

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean = this::class.isInstance(other) && other is Identifiable && other.id == id

}
